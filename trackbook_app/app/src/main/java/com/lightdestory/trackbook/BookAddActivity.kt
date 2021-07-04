package com.lightdestory.trackbook

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.lightdestory.trackbook.collection.Library
import com.lightdestory.trackbook.databinding.BookAddBinding
import com.lightdestory.trackbook.models.User
import com.lightdestory.trackbook.network.APIBuddy
import com.lightdestory.trackbook.sensor.ShakeSensorEventListener
import com.lightdestory.trackbook.utils.DataChecker
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


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
        binding.addISBNScan.setOnClickListener { scan("isbn") }
        binding.addTitleScan.setOnClickListener { scan("title") }
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

    private fun isPermitted(): Boolean {
        return when (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1)
    }

    private fun scan(scanType: String) {
        if (!isPermitted()) {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_warning)
                .setTitle(R.string.add_PermissionTitle)
                .setMessage(R.string.add_PermissionDesc)
                .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.dialog_Yes) { dialog, _ ->
                    dialog.dismiss()
                    requestPermission()
                }.show()
            return
        }
        if (scanType == "isbn")
            scanISBN()
        else
            dispatchTakePictureIntent()
    }

    private fun scanISBN() {
        val integrator: IntentIntegrator = IntentIntegrator(this)
        integrator.setPrompt(getString(R.string.add_scanISBNTitle))
            .setOrientationLocked(false)
            .setTorchEnabled(true)
            .setDesiredBarcodeFormats(IntentIntegrator.EAN_13, IntentIntegrator.EAN_8)
            .initiateScan()
    }

    private fun add() {
        if (!isDataGood()) {
            return
        }
        val alert = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
        val isbn: String = binding.addISBNInput.editText?.text.toString()
        if (Library.instance.findBookByISBN(isbn)) {
            alert.setIcon(R.drawable.icon_warning)
                .setTitle(R.string.add_existBookTitle)
                .setMessage(R.string.add_existBookDesc)
                .setPositiveButton(R.string.dialog_OK) { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }
        val title: String = binding.addTitleInput.editText?.text.toString()
        val color: String = colorList.indexOf(colorPicker.text.toString()).toString()
        val page_read: Int = binding.addPageReadInput.editText?.text.toString().toInt()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start_read: String = sdf.format(Date())
        Library.instance.addBook(this, isbn, title, color, page_read, start_read)
        alert.setIcon(R.drawable.icon_success)
            .setTitle(R.string.add_savedBookTitle)
            .setMessage(R.string.add_savedBookDesc)
            .setPositiveButton(R.string.dialog_OK) { dialog, _ ->
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

    private fun bookTitleLookUp(isbn: String) {
        val progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.endpoint_loading))
        progress.show()
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.endpoint_book_title)
            .setCancelable(false)
        val req: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, getString(R.string.endpoint_book).replace("$1", isbn), null,
            { response ->
                progress.dismiss()
                val title: String = response.getString("result")
                dialog.setMessage(getString(R.string.add_scannedDB).replace("$1", title))
                dialog.setIcon(R.drawable.icon_success)
                    .setNeutralButton(R.string.dialog_OK) { dialog, _ ->
                        dialog.dismiss()
                        binding.addTitleInput.editText?.setText(title)
                    }
                    .show()
            }, { error ->
                progress.dismiss()
                if (error?.networkResponse != null && error.networkResponse.data.decodeToString()
                        .isNotEmpty()
                ) {
                    val err = JSONObject(error.networkResponse.data.decodeToString())
                    dialog.setIcon(R.drawable.icon_warning)
                        .setMessage(err.getString("result"))
                } else {
                    dialog.setIcon(R.drawable.icon_error)
                        .setMessage(R.string.endpoint_error)
                }
                dialog.setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                dialog.show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = User.instance.token
                return headers
            }
        }
        req.tag = getString(R.string.endpoint_book_tag)
        APIBuddy.getInstance(this).addRequest(req)
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
        )
    }

    override fun onStop() {
        super.onStop()
        APIBuddy.getInstance(this).stopPending(getString(R.string.endpoint_book_tag))
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(mSensorListener);
    }

    private fun ocr(image: Bitmap) {

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, 55)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val dialogInfo = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val scanResult: IntentResult? =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            dialogInfo.setTitle(R.string.add_scanISBNTitle)
            if (scanResult?.contents != null) {
                var code: String = scanResult.contents.toString()
                if (code.length == 10) code = "978$code"
                binding.addISBNInput.editText?.setText(code)
                dialogInfo.setIcon(R.drawable.icon_info)
                    .setMessage(R.string.add_scanDBLookUp)
                    .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton(R.string.dialog_Yes) { dialog, _ ->
                        dialog.dismiss()
                        bookTitleLookUp(code)
                    }
            } else {
                dialogInfo.setIcon(R.drawable.icon_warning)
                    .setMessage(R.string.add_scanNoResult)
                    .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
            }
            dialogInfo.show()
        } else if (requestCode == 55) {
            if (resultCode == RESULT_OK) {
                val tmpBtm = data?.extras?.get("data") as Bitmap
                val image = InputImage.fromBitmap(tmpBtm, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val progress = ProgressDialog(this)
                progress.setMessage(getString(R.string.endpoint_loading))
                progress.show()
                val result: Task<Text> = recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val filtered = visionText.text.replace("\n"," ")
                        progress.dismiss()
                        MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.add_scanTitleTitle)
                            .setCancelable(false)
                            .setIcon(R.drawable.icon_success)
                            .setMessage(getString(R.string.add_scannedTitle).replace("$1", filtered))
                            .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(R.string.dialog_Yes) {dialog, _ ->
                                dialog.dismiss()
                                binding.addTitleInput.editText?.setText(filtered)
                            }
                            .show()
                    }
                    .addOnFailureListener { e ->
                        progress.dismiss()
                        MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.add_scanTitleTitle)
                            .setCancelable(false)
                            .setIcon(R.drawable.icon_error)
                            .setMessage(e.message)
                            .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                dialogInfo.setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
            } else {
                dialogInfo.setTitle(R.string.add_scanTitleTitle)
                    .setIcon(R.drawable.icon_warning)
                    .setMessage(R.string.add_scanNoResult)
                    .setNeutralButton(R.string.dialog_OK) { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}