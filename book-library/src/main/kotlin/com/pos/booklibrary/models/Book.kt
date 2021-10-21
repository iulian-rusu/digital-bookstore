package com.pos.booklibrary.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "books")
class Book(
    @Id
    @Column(name = "isbn")
    private var isbn: String = "",

    @Column(name = "title")
    private var title: String = "",

    @Column(name = "publisher")
    private var publisher: String = "",

    @Column(name = "publish_year")
    private var publishYear: Int = -1,

    @Column(name = "genre")
    private var genre: String = ""
) {
    fun getIsbn() = isbn

    fun setIsbn(value: String) {
        isbn = value
    }

    fun getTitle() = title

    fun setTitle(value: String) {
        title = value
    }

    fun getPublisher() = publisher

    fun setPublisher(value: String) {
        publisher = value
    }

    fun getPublishYear() = publishYear

    fun setPublishYear(value: Int) {
        publishYear = value
    }

    fun getGenre() = genre

    fun setGenre(value: String) {
        genre = value
    }
}
