package com.pos.booklibrary.models

data class BookOrder(
    var date: String,
    var items: List<BookOrderEntry>
)
