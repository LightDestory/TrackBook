package com.lightdestory.trackbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.databinding.BookAddBinding
import com.lightdestory.trackbook.databinding.BookEditBinding
import com.lightdestory.trackbook.models.BookReading

class BookAddActivity : AppCompatActivity() {

    private lateinit var binding: BookAddBinding
    private lateinit var colorPicker: AutoCompleteTextView
    private lateinit var colorList: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = BookAddBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_AddBook)
        colorList = resources.getStringArray(R.array.book_colors_name)
        val adapter = ArrayAdapter(this, R.layout.list_item, colorList)
        colorPicker = binding.addMenuColors.editText as AutoCompleteTextView
        colorPicker.setAdapter(adapter)
        colorPicker.setOnItemClickListener { _, _, position, _ -> changeBookColor(position) }
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun changeBookColor(colorID: Int) {
        val color: Int = this.resources.getIntArray(R.array.book_colors)[colorID]
        binding.addColorCardIcon.drawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                color,
                BlendModeCompat.SRC_ATOP
            )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}