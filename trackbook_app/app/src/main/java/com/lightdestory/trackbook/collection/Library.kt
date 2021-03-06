package com.lightdestory.trackbook.collection

import android.content.SharedPreferences
import android.content.Context
import com.google.gson.Gson
import com.lightdestory.trackbook.R
import com.lightdestory.trackbook.models.BookReading
import com.lightdestory.trackbook.utils.SharedPreferencesSingleton

class Library private constructor() {

    val books: ArrayList<BookReading> = ArrayList()

    companion object {
        val instance: Library by lazy { Library() }
    }

    fun addBook(context: Context, book: BookReading) {
        books.add(book)
        saveLibrary(context)
    }

    fun findBookByISBN(isbn: String): Boolean {
        books.forEach { bookReading ->
            if(bookReading.isbn == isbn)
                return true
        }
        return false
    }

    fun addBook(context: Context, isbn: String, title: String, color: String, page_read: Int, start_read: String) {
        books.add(BookReading(isbn, title, start_read, page_read, color))
        saveLibrary(context)
    }

    fun loadBooks(context: Context, newBooks: List<BookReading>) {
        books.clear()
        books.addAll(newBooks)
        saveLibrary(context)
    }

    fun deleteBook(context: Context, index: Int) {
        books.removeAt(index)
        saveLibrary(context)
    }

    fun deleteAll(context: Context) {
        books.clear()
        saveLibrary(context)
    }

    fun jsonLibrary(): String {
        val gson: Gson = Gson()
        return gson.toJson(books)
    }

    fun saveLibrary(context: Context) {
        val pref: SharedPreferences = SharedPreferencesSingleton.getInstance(context).preferences
        with(pref.edit()) {
            putString(context.getString(R.string.pref_Library), jsonLibrary())
            commit()
        }
    }

    init {
        val gson: Gson = Gson()
        books.addAll(gson.fromJson(SharedPreferencesSingleton.getInstance(null).preferences.getString("library", "[]"), Array<BookReading>::class.java)
            .toList())
    }
}