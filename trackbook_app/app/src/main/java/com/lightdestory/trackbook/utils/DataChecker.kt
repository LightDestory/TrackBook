package com.lightdestory.trackbook.utils

import java.math.BigInteger
import java.security.MessageDigest

class DataChecker private constructor() {

    private val patterns = arrayListOf<String>(
        android.util.Patterns.EMAIL_ADDRESS.pattern(),
        "^[a-zA-Z0-9\\.\\,\\#\\?\\-\\_]{8,}$",
        "^[a-zA-Z0-9]{3,16}$",
        "^[A-Za-z0-9 \',-_.]{2,}$",
        "^[0-9]+$"
    )

    companion object {
        const val DATA_TYPE_EMAIL = 0
        const val DATA_TYPE_PASSWORD = 1
        const val DATA_TYPE_PEN_NAME = 2
        const val DATA_TYPE_BOOK_TITLE = 3
        const val DATA_TYPE_BOOK_PAGE = 4
        private var instance: DataChecker? = null
        fun getInstance(): DataChecker {
            if (instance == null)
                instance = DataChecker()
            return instance!!
        }

    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun checkData(data: String, dataType: Int): Boolean {
        return Regex(patterns[dataType]).matches(data)
    }
}