package com.lightdestory.trackbook

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
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
        binding.bookshelfFloatMenuAdd.setOnClickListener { goAdd(); fabToggle() }
        setContentView(binding.root)
    }

    private fun goAdd() {
        val goForward: Intent = Intent(this, BookAddActivity::class.java)
        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair<View, String>(binding.bookshelfBanner, getString(R.string.transition_Logo))
        )
        startActivity(goForward, options.toBundle())
    }

    private fun miniFabDeleteAll() {
        val alert: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        alert.setCancelable(false)
            .setTitle(R.string.bookshelf_DeleteBookTitle)
            .setIcon(R.drawable.icon_warning)
        if(Library.instance.books.size > 0){
            alert.setMessage(R.string.bookshelf_DeleteAllBooksDesc)
                .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss()}
                .setPositiveButton(R.string.dialog_Yes) {dialog, _ ->
                    Library.instance.deleteAll(this)
                    LibraryAdapter.instance.notifyDataSetChanged()
                    checkBookshelfEmpty()
                    dialog.dismiss()
                }
        } else {
            alert.setMessage(R.string.bookshelf_EmptyDesc)
                .setNeutralButton(R.string.dialog_OK) {dialog, _ -> dialog.dismiss()}
        }
        alert.show()
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

    fun checkBookshelfEmpty() {
        if(Library.instance.books.size == 0){
            binding.bookshelfListContainer.visibility = View.GONE
            binding.bookshelfEmpty.visibility = View.VISIBLE
        }
        else {
            binding.bookshelfListContainer.visibility = View.VISIBLE
            binding.bookshelfEmpty.visibility = View.GONE
        }
    }

    private fun fabToggle() {
        isFabOpen = !isFabOpen
        val icon: Int = if(isFabOpen) R.drawable.icon_close else R.drawable.icon_menu
        binding.bookshelfFloatMenu.setImageDrawable(getDrawable(icon))
        setMiniFabVisibility()
        setMiniFabAnimations()
    }

    override fun onResume() {
        super.onResume()
        checkBookshelfEmpty()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}