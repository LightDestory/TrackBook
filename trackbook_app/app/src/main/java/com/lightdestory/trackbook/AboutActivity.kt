package com.lightdestory.trackbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lightdestory.trackbook.databinding.AboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: AboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_About)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}