package com.lightdestory.trackbook

import android.content.Intent
import android.net.Uri
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
        binding.aboutGitHubCard.setOnClickListener { visitRepository() }
        setContentView(binding.root)
    }

    private fun visitRepository(){
        val goRepo: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_GitHubLink)))
        startActivity(goRepo)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}