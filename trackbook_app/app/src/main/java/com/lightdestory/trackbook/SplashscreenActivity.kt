package com.lightdestory.trackbook

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.animation.AnimationUtils
import com.lightdestory.trackbook.databinding.SplashscreenBinding

class SplashscreenActivity : AppCompatActivity() {

    private val _SPLASHSCREEN: Long = 3000
    private lateinit var binding: SplashscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun checkLogin() {
        lateinit var goForward: Intent
        val pref: SharedPreferences = getPreferences(MODE_PRIVATE)
        if (pref.contains(getString(R.string.pref_login))) {
            goForward = Intent(this, DashboardActivity::class.java)
            startActivity(goForward)
        } else {
            goForward = Intent(this, LoginActivity::class.java)
            val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, Pair<View, String>(binding.splashImage, getString(R.string.transition_Logo)))
            startActivity(goForward, options.toBundle())
        }
    }

    private fun setAnimations() {
        val topLeft = AnimationUtils.loadAnimation(this, R.anim.fade_in_from_topleft)
        val left = AnimationUtils.loadAnimation(this, R.anim.fade_in_from_left)
        val right = AnimationUtils.loadAnimation(this, R.anim.fade_in_from_right)
        binding.splashImage.animation = topLeft
        binding.splashTitle.animation = left
        binding.splashSlogan.animation = right

    }

    override fun onResume() {
        super.onResume()
        setAnimations()
        Handler(mainLooper).postDelayed({
            checkLogin()
        }, _SPLASHSCREEN)
    }

}