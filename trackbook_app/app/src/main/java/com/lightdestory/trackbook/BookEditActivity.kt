package com.lightdestory.trackbook

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.collection.LibraryAdapter
import com.lightdestory.trackbook.databinding.BookEditBinding
import com.lightdestory.trackbook.models.BookReading
import com.lightdestory.trackbook.sensor.ShakeSensorEventListener
import com.lightdestory.trackbook.utils.DataChecker

class BookEditActivity : AppCompatActivity() {
    private lateinit var binding: BookEditBinding
    private lateinit var editing: BookReading
    private lateinit var colorPicker: AutoCompleteTextView
    private lateinit var colorList: Array<String>
    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensorListener: ShakeSensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BookEditBinding.inflate(layoutInflater)
        binding.include.toolbar.navigationIcon = getDrawable(R.drawable.icon_back)
        binding.include.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.include.toolbarTitle.text = getString(R.string.label_EditBook)
        editing = Library.instance.books[intent.extras?.getInt("book_position")!!]
        colorList = resources.getStringArray(R.array.book_colors_name)
        val adapter = ArrayAdapter(this, R.layout.list_item, colorList)
        colorPicker = binding.editMenuColors.editText as AutoCompleteTextView
        colorPicker.setAdapter(adapter)
        colorPicker.setOnItemClickListener { _, _, position, _ -> changeBookColor(position) }
        binding.editTitleInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (binding.editTitleInput.error != null && hasFocus)
                binding.editTitleInput.error = null
        }
        binding.editPageReadInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (binding.editPageReadInput.error != null && hasFocus)
                binding.editPageReadInput.error = null
        }
        mSensorListener =
            ShakeSensorEventListener(this, editing.page_read, binding.editPageReadInput.editText!!)
        mSensorManager =
            getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        mSensorManager.registerListener(
            mSensorListener,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        binding.editPageReadInput.editText!!.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val value = v.editableText.toString()
                if (value.isEmpty() || value.toIntOrNull() == null) {
                    mSensorListener.counter = 0
                    v.text = "0"
                } else {
                    mSensorListener.counter = v.editableText.toString().toInt()
                }
            }
            false
        }
        binding.editSave.setOnClickListener { saveData() }
        initUI()
        setContentView(binding.root)
    }

    private fun areCredentialsGood(): Boolean {
        val titleMatch: Boolean = DataChecker.getInstance().checkData(
            binding.editTitleInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_BOOK_TITLE
        )
        val pageMatch: Boolean = DataChecker.getInstance().checkData(
            binding.editPageReadInput.editText?.text.toString(),
            DataChecker.DATA_TYPE_BOOK_PAGE
        )
        var errorMessage = ""
        if (!titleMatch) {
            binding.editTitleInput.error = getString(R.string.check_InvalidTitleBookTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidTitleBookDesc)}\n"
        }
        if (!pageMatch) {
            binding.editPageReadInput.error = getString(R.string.check_InvalidPageBookTitle)
            errorMessage += "\n - ${getString(R.string.check_InvalidPageBookDesc)}\n"
        }
        if (!titleMatch || !pageMatch) {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_error)
                .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                .setMessage(errorMessage)
                .setTitle(getString(R.string.edit_InvalidBookData))
                .setCancelable(false)
                .show()
            return false
        }
        return true
    }

    private fun saveData() {
        if (!areCredentialsGood()) {
            return
        }
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.icon_warning)
            .setCancelable(false)
            .setTitle(R.string.edit_overwriteBookTitle)
            .setMessage(R.string.edit_overwriteBookDesc)
            .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.dialog_Yes) { dialog, _ ->
                dialog.dismiss()
                editing.title = binding.editTitleInput.editText?.text.toString()
                editing.color = colorList.indexOf(colorPicker.text.toString()).toString()
                editing.page_read = binding.editPageReadInput.editText?.text.toString().toInt()
                LibraryAdapter.instance.notifyItemChanged(intent.extras?.getInt("book_position")!!)
                onBackPressed()
            }.show()
    }

    private fun changeBookColor(colorID: Int) {
        val color: Int = this.resources.getIntArray(R.array.book_colors)[colorID]
        binding.editColorCardIcon.drawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                color,
                BlendModeCompat.SRC_ATOP
            )
    }

    private fun initUI() {
        changeBookColor(editing.color.toInt())
        colorPicker.setText(colorList[editing.color.toInt()], false)
        binding.editTitleInput.editText?.setText(editing.title)
        binding.editPageReadInput.editText?.setText(editing.page_read.toString())
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