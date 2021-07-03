package com.lightdestory.trackbook.collection

import android.content.SharedPreferences
import android.content.Context
import com.google.gson.Gson
import com.lightdestory.trackbook.R
import com.lightdestory.trackbook.models.BookReading
import com.lightdestory.trackbook.utils.SharedPreferencesSingleton

class Library private constructor() {

    private val books: ArrayList<BookReading> = ArrayList()

    companion object {
        val instance: Library by lazy { Library() }
    }

    fun loadBooks(context: Context, newBooks: List<BookReading>) {
        books.clear()
        books.addAll(newBooks)
        saveLibrary(context)
    }

    fun deleteBook(index: Int) {
        books.removeAt(index)
    }

    private fun testData() {
        books.add(BookReading("1234567890123", "Test1", "2021-07-03", 32, "1"))
        books.add(BookReading("1234567890124", "Test1", "2021-07-02", 21, "2"))
    }

    fun jsonLibrary(): String {
        val gson: Gson = Gson()
        return gson.toJson(books)
    }

    init {
        testData()
    }

    private fun saveLibrary(context: Context) {
        val pref: SharedPreferences = SharedPreferencesSingleton.getInstance(context).preferences
        with(pref.edit()) {
            putString(context.getString(R.string.pref_Library), jsonLibrary())
            commit()
        }
    }
}