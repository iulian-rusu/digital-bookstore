package com.pos.booklibrary.models

import javax.persistence.*

@Entity
@Table(name = "book_authors")
@IdClass(BookAuthorId::class)
class BookAuthor(
    @Id
    @Column(name = "isbn")
    private var isbn: String = "",

    @Id
    @Column(name = "author_id")
    private var authorId: Long = -1,

    @Column(name = "author_index")
    private var authorIndex: Long = 0
) {
    fun getIsbn() = isbn

    fun setIsbn(value: String) {
        isbn = value
    }

    fun getAuthorId() = authorId

    fun setAuthorId(value: Long) {
        authorId = value
    }

    fun getAuthorIndex() = authorIndex

    fun setAuthorIndex(value: Long) {
        authorId = value
    }

    fun getId() = BookAuthorId(isbn, authorId)
}