package com.pos.booklibrary.interfaces

import com.pos.booklibrary.models.Book
import com.pos.booklibrary.models.BookAuthor
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface BookAccessInterface {
    fun getAllBooks(): CollectionModel<EntityModel<Book>>
    fun getBook(isbn: String): ResponseEntity<EntityModel<Book>>
    fun postBook(newBook: Book): ResponseEntity<EntityModel<Book>>
    fun putBook(isbn: String, newBook: Book): ResponseEntity<EntityModel<Book>>
    fun deleteBook(isbn: String): ResponseEntity<Unit>
    fun getBookAuthors(isbn: String): CollectionModel<EntityModel<BookAuthor>>
    fun getBookAuthor(isbn: String, index: Long): ResponseEntity<EntityModel<BookAuthor>>
    fun postBookAuthors(isbn: String, bookAuthorIds: List<Long>): ResponseEntity<CollectionModel<EntityModel<BookAuthor>>>
    fun deleteBookAuthor(isbn: String, index: Long): ResponseEntity<Unit>
    fun deleteBookAuthors(isbn: String): ResponseEntity<Unit>
}