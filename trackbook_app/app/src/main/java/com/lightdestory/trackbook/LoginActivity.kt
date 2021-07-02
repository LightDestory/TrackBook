package com.lightdestory.trackbook

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.lightdestory.trackbook.databinding.LoginBinding
import com.lightdestory.trackbook.models.User
import com.lightdestory.trackbook.network.APIBuddy
import com.lightdestory.trackbook.utils.DataChecker
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginEmailInput.editText?.setOnFocusChangeListener() { _, hasFocus ->
            if(binding.loginEmailInput.error != null && hasFocus)
                binding.loginEmailInput.error = null
        }
        binding.loginPasswordInput.editText?.setOnFocusChangeListener() { _, hasFocus ->
            if(binding.loginPasswordInput.error != null && hasFocus)
                binding.loginPasswordInput.error = null
        }

        binding.loginAccess.setOnClickListener { login() }
        binding.loginNewUser.setOnClickListener { goToForm("register") }
        binding.loginForgotPass.setOnClickListener { goToForm("reset") }
    }

    private fun areCredentialsGood(): Boolean {
        val emailMatch: Boolean = DataChecker.getInstance().checkData(binding.loginEmailInput.editText?.text.toString(), DataChecker.DATA_TYPE_EMAIL)
        val passwordMatch: Boolean = DataChecker.getInstance().checkData(binding.loginPasswordInput.editText?.text.toString(), DataChecker.DATA_TYPE_PASSWORD)
        var errorMessage = ""
        if(!emailMatch) {
            binding.loginEmailInput.error = getString(R.string.check_InvalidEmailTitle)
            errorMessage+= "\n - ${getString(R.string.check_InvalidEmailDesc)}\n"
        }
        if(!passwordMatch) {
            binding.loginPasswordInput.error = getString(R.string.check_InvalidPasswordTitle)
            errorMessage+= "\n - ${getString(R.string.check_InvalidPasswordDesc)}\n"
        }
        if(!emailMatch || !passwordMatch) {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.icon_error)
                .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                .setMessage(errorMessage)
                .setTitle(getString(R.string.credentials_Error))
                .show()
            return false
        }
        return true
    }

    private fun login(){
        if(!areCredentialsGood()){
            return
        }
        val progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.endpoint_loading))
        progress.show()
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.endpoint_login_title)
        val params = JSONObject()
            .put("email", binding.loginEmailInput.editText?.text.toString())
            .put("password", DataChecker.getInstance().md5(binding.loginPasswordInput.editText?.text.toString()))
        val req: JsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, getString(R.string.endpoint_login), params,
            { response ->
                val user: String = response.getString("result")
                val pref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
                with(pref.edit()){
                    putString(getString(R.string.pref_User), user)
                    commit()
                }
                progress.dismiss()
                goDashboard()
            }
        ) { error ->
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
        }
        req.tag = getString(R.string.endpoint_login_tag)
        APIBuddy.getInstance(this).addRequest(req)
    }

    private fun goToForm(formName: String){
        val dest = if (formName == "register") RegisterActivity::class.java else ResetPasswordActivity::class.java
        val form: Intent = Intent(this, dest)
        val transitions = ArrayList<Pair<View, String>>()
        transitions.add(Pair(binding.loginBanner, getString(R.string.transition_Logo)))
        transitions.add(Pair(binding.loginTitle, getString(R.string.transition_Title)))
        transitions.add(Pair(binding.loginSubtitle, getString(R.string.transition_Subtitle)))
        transitions.add(Pair(binding.loginCard, getString(R.string.transition_Card)))
        transitions.add(Pair(binding.loginEmailInput, getString(R.string.transition_Email)))
        transitions.add(Pair(binding.loginPasswordInput, getString(R.string.transition_Password)))
        transitions.add(Pair(binding.loginAccess, getString(R.string.transition_FormButton)))
        transitions.add(Pair(binding.loginNewUser, getString(R.string.transition_FormAlt)))
        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, *transitions.toTypedArray())
        startActivity(form, options.toBundle())
    }

    private fun goDashboard() {
        val goDashboard: Intent = Intent(this, DashboardActivity::class.java)
        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, Pair<View, String>(binding.loginBanner, getString(R.string.transition_Logo)))
        startActivity(goDashboard, options.toBundle())
        // workaround for home screen issue during activity switch
        Handler(mainLooper).postDelayed({
            finish()
        }, 1000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStop() {
        super.onStop()
        APIBuddy.getInstance(this).stopPending(getString(R.string.endpoint_login_tag))
    }
}