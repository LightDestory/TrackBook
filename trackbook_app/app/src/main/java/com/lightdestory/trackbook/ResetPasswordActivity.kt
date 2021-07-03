package com.lightdestory.trackbook

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lightdestory.trackbook.databinding.RegisterBinding
import com.lightdestory.trackbook.databinding.ResetPasswordBinding
import com.lightdestory.trackbook.network.APIBuddy
import com.lightdestory.trackbook.utils.DataChecker
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetEmailInput.editText?.setOnFocusChangeListener() { _, hasFocus ->
            if (binding.resetEmailInput.error != null && hasFocus)
                binding.resetEmailInput.error = null
        }
        binding.resetNewPasswordInput.editText?.setOnFocusChangeListener() { _, hasFocus ->
            if (binding.resetNewPasswordInput.error != null && hasFocus)
                binding.resetNewPasswordInput.error = null
        }
        binding.resetPenNameInput.editText?.setOnFocusChangeListener() { _, hasFocus ->
            if (binding.resetPenNameInput.error != null && hasFocus)
                binding.resetPenNameInput.error = null
        }
        binding.resetPassRemember.setOnClickListener { onBackPressed() }
        binding.resetAccess.setOnClickListener { change() }

    }

    private fun areCredentialsGood(): Boolean {
        val emailMatch: Boolean = DataChecker.getInstance().checkData(
            binding.resetEmailInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_EMAIL
        )
        val passwordMatch: Boolean = DataChecker.getInstance().checkData(
            binding.resetNewPasswordInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_PASSWORD
        )
        val penNameMatch: Boolean = DataChecker.getInstance().checkData(
            binding.resetPenNameInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_PEN_NAME
        )
        var errorMessage = ""
        if (!emailMatch) {
            binding.resetEmailInput.error = getString(R.string.check_InvalidEmailTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidEmailDesc)}\n"
        }
        if (!passwordMatch) {
            binding.resetNewPasswordInput.error = getString(R.string.check_InvalidPasswordTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidPasswordDesc)}\n"
        }
        if (!penNameMatch) {
            binding.resetPenNameInput.error = getString(R.string.check_InvalidPenNameTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidPenNameDesc)}\n"
        }
        if (!emailMatch || !passwordMatch || !penNameMatch) {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_error)
                .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                .setMessage(errorMessage)
                .setTitle(getString(R.string.credentials_Error))
                .setCancelable(false)
                .show()
            return false
        }
        return true
    }

    private fun change() {
        if (!areCredentialsGood()) {
            return
        }
        val progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.endpoint_loading))
        progress.show()
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.endpoint_reset_title)
            .setCancelable(false)
        val params = JSONObject()
            .put("email", binding.resetEmailInput.editText?.text.toString())
            .put(
                "password",
                DataChecker.getInstance()
                    .md5(binding.resetNewPasswordInput.editText?.text.toString())
            )
            .put("penName", binding.resetPenNameInput.editText?.text.toString())
        val req: JsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, getString(R.string.endpoint_reset), params,
            { response ->
                progress.dismiss()
                dialog.setMessage(response.getString("result"))
                    .setIcon(R.drawable.icon_success)
                    .setNeutralButton(R.string.dialog_OK) { dialog, _ ->
                        dialog.dismiss()
                        this.onBackPressed()
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
                        .setMessage(getString(R.string.endpoint_error))
                }
                dialog.setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                dialog.show()
            })
        req.tag = getString(R.string.endpoint_reset_tag)
        APIBuddy.getInstance(this).addRequest(req)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStop() {
        super.onStop()
        APIBuddy.getInstance(this).stopPending(getString(R.string.endpoint_reset_tag))
    }
}