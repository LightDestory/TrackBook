package com.lightdestory.trackbook

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.databinding.BookAddBinding
import com.lightdestory.trackbook.sensor.ShakeSensorEventListener
import com.lightdestory.trackbook.utils.DataChecker
import java.text.SimpleDateFormat
import java.util.*


class BookAddActivity : AppCompatActivity() {

    private lateinit var binding: BookAddBinding
    private lateinit var colorPicker: AutoCompleteTextView
    private lateinit var colorList: Array<String>
    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensorListener: ShakeSensorEventListener
    private var counter: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = BookAddBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_AddBook)
        colorList = resources.getStringArray(R.array.book_colors_name)
        val adapter = ArrayAdapter(this, R.layout.list_item, colorList)
        binding.addPageReadInput.editText?.setText(counter.toString())
        colorPicker = binding.addMenuColors.editText as AutoCompleteTextView
        colorPicker.setAdapter(adapter)
        colorPicker.setOnItemClickListener { _, _, position, _ -> changeBookColor(position) }
        super.onCreate(savedInstanceState)
        colorPicker.setText(colorList[0], false)
        changeBookColor(0)
        binding.addISBNInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (binding.addISBNInput.error != null && hasFocus) {
                binding.addISBNInput.error = null
            }
        }
        binding.addTitleInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (binding.addTitleInput.error != null && hasFocus) {
                binding.addTitleInput.error = null
            }
        }
        binding.addPageReadInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (binding.addPageReadInput.error != null && hasFocus) {
                binding.addPageReadInput.error = null
            }
        }
        mSensorListener =
            ShakeSensorEventListener(this, counter, binding.addPageReadInput.editText!!)
        mSensorManager =
            getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        mSensorManager.registerListener(
            mSensorListener,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        binding.addSave.setOnClickListener { add() }
        setContentView(binding.root)
    }

    private fun isDataGood(): Boolean {
        val titleMatch: Boolean = DataChecker.getInstance().checkData(
            binding.addTitleInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_BOOK_TITLE
        )
        val pageMatch: Boolean = DataChecker.getInstance().checkData(
            binding.addPageReadInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_BOOK_PAGE
        )
        val isbnMatch: Boolean = DataChecker.getInstance().checkData(
            binding.addISBNInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_BOOK_ISBN
        )
        var errorMessage = ""
        if (!titleMatch) {
            binding.addTitleInput.error = getString(R.string.check_InvalidTitleBookTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidTitleBookDesc)}\n"
        }
        if (!pageMatch) {
            binding.addPageReadInput.error = getString(R.string.check_InvalidPageBookTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidPageBookDesc)}\n"
        }
        if (!isbnMatch) {
            binding.addISBNInput.error = getString(R.string.check_InvalidISBNBookTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidISBNBookDesc)}\n"
        }
        if (!titleMatch || !pageMatch || !isbnMatch) {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_error)
                .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                .setMessage(errorMessage)
                .setTitle(getString(R.string.book_InvalidBookData))
                .setCancelable(false)
                .show()
            return false
        }
        return true
    }

    private fun add() {
        if(!isDataGood()) {
            return
        }
        val isbn: String = binding.addISBNInput.editText?.text.toString()
        val title: String = binding.addTitleInput.editText?.text.toString()
        val color: String = colorList.indexOf(colorPicker.text.toString()).toString()
        val page_read: Int = binding.addPageReadInput.editText?.text.toString().toInt()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start_read: String = sdf.format(Date())
        Library.instance.addBook(this, isbn, title, color, page_read, start_read)
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.icon_success)
            .setTitle(R.string.add_savedBookTitle)
            .setMessage(R.string.add_savedBookDesc)
            .setPositiveButton(R.string.dialog_OK) {dialog, _ ->
                dialog.dismiss()
                onBackPressed()
            }.show()
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

    override fun onRestart() {
        super.onRestart()
        mSensorManager.registerListener(
            mSensorListener,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(mSensorListener);
    }
}