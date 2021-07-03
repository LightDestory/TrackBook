package com.lightdestory.trackbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lightdestory.trackbook.collection.LibraryAdapter
import com.lightdestory.trackbook.databinding.BookshelfBinding

class BookshelfActivity : AppCompatActivity() {

    private lateinit var binding: BookshelfBinding
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BookshelfBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_MyBooks)
        list = binding.bookshelfList
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = LibraryAdapter.instance
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}