package com.lightdestory.trackbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.lightdestory.trackbook.databinding.LoginBinding
import com.lightdestory.trackbook.databinding.SplashscreenBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginNewUser.setOnClickListener {
            val goRegister: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(goRegister)
        }
    }
}