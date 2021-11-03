package com.pos.booklibrary.models

class BriefBook(private var isbn: String = "", private var title: String = "") : BasicBook {
    constructor(book: Book) : this(book.getIsbn(), book.getTitle())

    override fun getIsbn() = isbn

    override fun setIsbn(value: String) {
        isbn = value
    }

    override fun getTitle() = title

    override fun setTitle(value: String) {
        title = value
    }
}