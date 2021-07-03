package com.lightdestory.trackbook

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.databinding.CloudBinding
import com.lightdestory.trackbook.models.BookReading
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
        binding.cloudBackUpCard.setOnClickListener { requestConfirm("backup") }
        binding.cloudRestoreCard.setOnClickListener { requestConfirm("restore") }
    }

    private fun requestConfirm(action: String){
        val titleID: Int = if(action == "backup") R.string.endpoint_backup_title else R.string.endpoint_restore_title
        val messageID: Int = if(action == "backup") R.string.cloud_backup_desc else R.string.cloud_restore_desc
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.icon_warning)
            .setCancelable(false)
            .setTitle(titleID)
            .setMessage(messageID)
            .setPositiveButton(R.string.dialog_Yes) {dialog, _ ->
                dialog.dismiss()
                if(action == "backup")
                    backUp()
                else
                    restore()
            }
            .setNegativeButton(R.string.dialog_No) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
        val req: JsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, getString(R.string.endpoint_backup_restore), params,
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
                if (error?.networkResponse != null && error.networkResponse.data.decodeToString().isNotEmpty()) {
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
        val progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.endpoint_loading))
        progress.show()
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.endpoint_restore_title)
            .setCancelable(false)
        val req: JsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, getString(R.string.endpoint_backup_restore), null,
            { response ->
                progress.dismiss()
                if(response.getString("result").contains("[")) {
                    val gson: Gson = Gson()
                    Library.instance.loadBooks(gson.fromJson(response.getString("result"), Array<BookReading>::class.java).toList())
                    dialog.setMessage(R.string.endpoint_restore_success)
                }
                else {
                    dialog.setMessage(response.getString("result"))
                }
                dialog.setIcon(R.drawable.icon_success)
                    .setNeutralButton(R.string.dialog_OK) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }, { error ->
                progress.dismiss()
                if (error?.networkResponse != null && error.networkResponse.data.decodeToString().isNotEmpty()) {
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
                return headers
            }
        }
        req.tag = getString(R.string.endpoint_restore_tag)
        APIBuddy.getInstance(this).addRequest(req)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStop() {
        super.onStop()
        APIBuddy.getInstance(this).stopPending(getString(R.string.endpoint_backup_tag))
        APIBuddy.getInstance(this).stopPending(getString(R.string.endpoint_restore_tag))
    }
}