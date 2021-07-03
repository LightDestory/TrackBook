package com.lightdestory.trackbook.models

data class BookReading(
    val isbn: String,
    var title: String,
    val start_read: String,
    var page_read: Int,
    var color: String
) {
}