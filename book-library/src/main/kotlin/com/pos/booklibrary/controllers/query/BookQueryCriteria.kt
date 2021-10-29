package com.pos.booklibrary.controllers.query

import com.pos.booklibrary.models.Book

data class BookQueryCriteria(
    var title: String = "",
    var publisher: String = "",
    var publishYear: Int = -1,
    var genre: String = "",
    var page: Int = -1,
    var itemsPerPage: Int = 10,
    var exactMatch: Boolean = false
) {
    constructor(queryParams: Map<String, String>) : this() {
        queryParams.apply {
            get("title")?.let { title = it }
            get("publisher")?.let { publisher = it }
            get("genre")?.let { genre = it }
            get("page")?.let { page = it.toInt() }
            get("items_per_page")?.let { itemsPerPage = it.toInt() }
            get("match")?.let { exactMatch = it == "exact" }
        }
    }

    fun check(book: Book) = if (exactMatch) checkExact(book) else checkPartial(book)

    private fun checkExact(book: Book): Boolean {
        if (title.isNotEmpty() && book.getTitle() != title)
            return false
        if (publisher.isNotEmpty() && book.getPublisher() != publisher)
            return false
        if (publishYear >= 0 && book.getPublishYear() != publishYear)
            return false
        if (genre.isNotEmpty() && book.getGenre() != genre)
            return false
        return true
    }

    private fun checkPartial(book: Book): Boolean {
        if (!book.getTitle().contains(title, ignoreCase = true))
            return false
        if (!book.getPublisher().contains(publisher, ignoreCase = true))
            return false
        if (publishYear >= 0 && book.getPublishYear() != publishYear)
            return false
        if (!book.getGenre().contains(genre, ignoreCase = true))
            return false
        return true
    }
}