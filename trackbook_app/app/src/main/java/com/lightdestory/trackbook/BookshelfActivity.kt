package com.lightdestory.trackbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.collection.LibraryAdapter
import com.lightdestory.trackbook.databinding.BookshelfBinding

class BookshelfActivity : AppCompatActivity() {

    private lateinit var binding: BookshelfBinding
    private lateinit var list: RecyclerView
    private var isFabOpen: Boolean = false
    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fab_fade_in_from_bottom)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fab_fade_out_to_bottom)
        binding = BookshelfBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_MyBooks)
        list = binding.bookshelfList
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = LibraryAdapter.instance
        binding.bookshelfFloatMenu.setOnClickListener { fabToggle() }
        binding.bookshelfFloatMenuRemoveAll.setOnClickListener { miniFabDeleteAll(); fabToggle() }
        setContentView(binding.root)
    }

    private fun miniFabDeleteAll() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(R.string.bookshelf_DeleteBookTitle)
            .setIcon(R.drawable.icon_warning)
            .setMessage(R.string.bookshelf_DeleteAllBooksDesc)
            .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss()}
            .setPositiveButton(R.string.dialog_Yes) {dialog, _ ->
                Library.instance.deleteAll(this)
                LibraryAdapter.instance.notifyDataSetChanged()
                dialog.dismiss()
            }.show()
    }

    private fun setMiniFabVisibility() {
        val state: Int = if(isFabOpen) View.VISIBLE else View.GONE
            binding.bookshelfFloatMenuRemoveAll.visibility = state
            binding.bookshelfFloatMenuAdd.visibility = state
    }

    private fun setMiniFabAnimations() {
        val animation: Animation = if(isFabOpen) fadeIn else fadeOut
            binding.bookshelfFloatMenuAdd.startAnimation(animation)
            binding.bookshelfFloatMenuRemoveAll.startAnimation(animation)
    }

    private fun fabToggle() {
        isFabOpen = !isFabOpen
        val icon: Int = if(isFabOpen) R.drawable.icon_close else R.drawable.icon_menu
        binding.bookshelfFloatMenu.setImageDrawable(getDrawable(icon))
        setMiniFabVisibility()
        setMiniFabAnimations()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}