package com.lightdestory.trackbook.collection

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.lightdestory.trackbook.models.BookReading

class Library {

    private constructor() {
        testData()
    }

    val books: ArrayList<BookReading> = ArrayList()

    companion object{
        val instance: Library by lazy { Library() }
    }

    fun loadBooks(newBooks: List<BookReading>) {
        books.clear()
        books.addAll(newBooks)
    }

    fun deleteBook(index: Int){
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
}