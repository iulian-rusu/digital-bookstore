package com.pos.booklibrary.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "book_authors")
@IdClass(BookAuthorIndex::class)
@JsonIgnoreProperties("id")
class BookAuthor(
    @Id
    @Column(name = "isbn")
    private var isbn: String = "",

    @Id
    @Column(name = "author_index")
    private var authorIndex: Long = -1,

    @Column(name = "author_id")
    private var authorId: Long = -1,
) {
    fun getIsbn() = isbn

    fun setIsbn(value: String) {
        isbn = value
    }

    fun getAuthorIndex() = authorIndex

    fun setAuthorIndex(value: Long) {
        authorIndex = value
    }

    fun getAuthorId() = authorId

    fun setAuthorId(value: Long) {
        authorId = value
    }
}