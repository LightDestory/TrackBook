package com.lightdestory.trackbook

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.lightdestory.trackbook.databinding.DashboardBinding
import com.lightdestory.trackbook.models.User
import com.lightdestory.trackbook.utils.SharedPreferencesSingleton

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: DashboardBinding
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardBinding.inflate(layoutInflater)
        binding.include.toolbarTitle.text = getString(R.string.dashboard_Name)
        val gson: Gson = Gson()
        user = gson.fromJson(
            SharedPreferencesSingleton.getInstance(this).preferences.getString(
                getString(R.string.pref_User),
                ""
            ), User::class.java
        )
        User.setUser(user)
        binding.dashboardUserTitle.text = User.instance.penName
        setContentView(binding.root)
        binding.dashboardLogOutCard.setOnClickListener { logOut() }
        binding.dashboardAboutCard.setOnClickListener { navigate("about") }
        binding.dashboardCloudCard.setOnClickListener { navigate("cloud") }
        binding.dashboardMyBooksCard.setOnClickListener { navigate("bookshelf") }
    }

    private fun navigate(destination: String) {
        val dest: Intent? = when (destination) {
            "about" -> Intent(this, AboutActivity::class.java)
            "login" -> Intent(this, LoginActivity::class.java)
            "cloud" -> Intent(this, CloudActivity::class.java)
            "bookshelf" -> Intent(this, BookshelfActivity::class.java)
            else -> null
        }
        val transitions = ArrayList<Pair<View, String>>()
        transitions.add(
            Pair<View, String>(
                binding.include.toolbar,
                getString(R.string.transition_AppBar)
            )
        )
        transitions.add(
            Pair<View, String>(
                binding.dashboardBanner,
                getString(R.string.transition_Logo)
            )
        )
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            *transitions.toTypedArray()
        )
        startActivity(dest, options.toBundle())
        if (destination == "login") {
            // workaround for home screen issue during activity switch
            Handler(mainLooper).postDelayed({
                finish()
            }, 1000)
        }
    }

    private fun logOut() {
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.icon_warning)
            .setTitle(R.string.logOut_Title)
            .setMessage(R.string.logOut_Desc)
            .setCancelable(false)
            .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.dialog_Yes) { dialog, _ ->
                val pref: SharedPreferences =
                    SharedPreferencesSingleton.getInstance(this).preferences
                with(pref.edit()) {
                    remove(getString(R.string.pref_User))
                    remove(getString(R.string.pref_Library))
                    commit()
                }
                dialog.dismiss()
                navigate("login")
            }
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
