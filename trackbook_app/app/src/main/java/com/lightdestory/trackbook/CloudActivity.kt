package com.lightdestory.trackbook

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonArray
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.databinding.CloudBinding
import com.lightdestory.trackbook.models.User
import com.lightdestory.trackbook.network.APIBuddy
import org.json.JSONArray
import org.json.JSONObject

class CloudActivity : AppCompatActivity() {

    private lateinit var binding: CloudBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CloudBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_Cloud)
        setContentView(binding.root)
        binding.cloudBackUpCard.setOnClickListener { backUp() }
        binding.cloudRestoreCard.setOnClickListener { restore() }
    }

    private fun backUp() {
        val progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.endpoint_loading))
        progress.show()
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.endpoint_backup_title)
            .setCancelable(false)
        val booksArray = JSONArray(Library.instance.jsonLibrary())
        val params = JSONObject()
            .put("books", booksArray)
        Toast.makeText(this, params.toString(), Toast.LENGTH_SHORT).show()
        val req: JsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, getString(R.string.endpoint_backup), params,
            { response ->
                progress.dismiss()
                dialog.setMessage(response.getString("result"))
                    .setIcon(R.drawable.icon_success)
                    .setNeutralButton(R.string.dialog_OK) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }, { error ->
                progress.dismiss()
                if (error?.networkResponse != null) {
                    val err = JSONObject(error.networkResponse.data.decodeToString())
                    dialog.setIcon(R.drawable.icon_warning)
                        .setMessage(err.getString("result"))
                } else {
                    dialog.setIcon(R.drawable.icon_error)
                        .setMessage(R.string.endpoint_error)
                }
                dialog.setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                dialog.show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = User.instance.token
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        req.tag = getString(R.string.endpoint_backup_tag)
        APIBuddy.getInstance(this).addRequest(req)
    }

    private fun restore() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStop() {
        super.onStop()
        APIBuddy.getInstance(this).stopPending(getString(R.string.endpoint_backup_tag))
    }
}