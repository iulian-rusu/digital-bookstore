package com.pos.booklibrary.interfaces

import com.pos.booklibrary.models.BasicBook
import com.pos.booklibrary.persistence.query.SearchBookQuery
import com.pos.booklibrary.models.Book
import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.persistence.query.UpdateOrderQuery
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface BookAccessInterface {
    fun getAllBooks(query: SearchBookQuery): ResponseEntity<CollectionModel<EntityModel<BasicBook>>>
    fun getBook(isbn: String, verbose: Boolean): ResponseEntity<EntityModel<BasicBook>>
    fun postOrder(query: UpdateOrderQuery): ResponseEntity<Unit>
    fun postBook(newBook: Book): ResponseEntity<EntityModel<BasicBook>>
    fun putBook(isbn: String, newBook: Book): ResponseEntity<EntityModel<BasicBook>>
    fun deleteBook(isbn: String): ResponseEntity<Unit>
    fun getBookAuthors(isbn: String): CollectionModel<EntityModel<BookAuthor>>
    fun getBookAuthor(isbn: String, index: Long): ResponseEntity<EntityModel<BookAuthor>>
    fun postBookAuthors(isbn: String, bookAuthorIds: List<Long>): ResponseEntity<CollectionModel<EntityModel<BookAuthor>>>
    fun deleteBookAuthor(isbn: String, index: Long): ResponseEntity<Unit>
    fun deleteBookAuthors(isbn: String): ResponseEntity<Unit>
}