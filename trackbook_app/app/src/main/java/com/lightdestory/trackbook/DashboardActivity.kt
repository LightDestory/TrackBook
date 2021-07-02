package com.lightdestory.trackbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.lightdestory.trackbook.databinding.DashboardBinding
import com.lightdestory.trackbook.models.User

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: DashboardBinding
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardBinding.inflate(layoutInflater)
        binding.include.toolbarTitle.text = getString(R.string.dashboard_Name)
        val gson: Gson = Gson()
        user = gson.fromJson(getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString(getString(R.string.pref_User), ""), User::class.java)
        binding.dashboardUserTitle.text = user.penName
        setContentView(binding.root)
    }
}