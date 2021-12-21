package com.pos.booklibrary.business.models

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
    private var genre: String = "",

    @Column(name = "price")
    private var price: Int = 0,

    @Column(name = "stock")
    private var stock: Int = 1
) : BasicBook {
    override fun getIsbn() = isbn
    override fun setIsbn(value: String) {
        isbn = value
    }

    override fun getTitle() = title
    override fun setTitle(value: String) {
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

    fun getPrice() = price
    fun setPrice(value: Int) {
        price = value
    }

    fun getStock() = stock
    fun setStock(value: Int) {
        stock = value
    }
}