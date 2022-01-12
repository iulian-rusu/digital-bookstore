package com.pos.booklibrary.business.services.interfaces

import com.pos.booklibrary.api.requests.StockUpdateRequest
import com.pos.booklibrary.business.models.Author
import com.pos.booklibrary.business.models.BasicBook
import com.pos.booklibrary.business.models.Book
import com.pos.booklibrary.persistence.query.SearchBookQuery
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface BookAccessInterface {
    fun getAllBooks(query: SearchBookQuery): ResponseEntity<CollectionModel<EntityModel<BasicBook>>>
    fun getBook(isbn: String, verbose: Boolean): ResponseEntity<EntityModel<BasicBook>>
    fun postBook(newBook: Book, token: String?): ResponseEntity<EntityModel<BasicBook>>
    fun putBook(isbn: String, newBook: Book, token: String?): ResponseEntity<EntityModel<BasicBook>>
    fun deleteBook(isbn: String, token: String?): ResponseEntity<Unit>
    fun getBookAuthors(isbn: String): CollectionModel<EntityModel<Author>>
    fun getBookAuthor(isbn: String, index: Long): ResponseEntity<EntityModel<Author>>
    fun postBookAuthors(
        isbn: String,
        bookAuthorIds: List<Long>,
        token: String?
    ): ResponseEntity<CollectionModel<EntityModel<Author>>>

    fun deleteBookAuthor(isbn: String, index: Long, token: String?): ResponseEntity<Unit>
    fun deleteBookAuthors(isbn: String, token: String?): ResponseEntity<Unit>
    fun postStockUpdate(request: StockUpdateRequest, token: String?): ResponseEntity<CollectionModel<EntityModel<BasicBook>>>
}