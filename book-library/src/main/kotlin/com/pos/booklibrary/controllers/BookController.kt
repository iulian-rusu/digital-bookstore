package com.pos.booklibrary.controllers

import com.pos.booklibrary.models.BasicBook
import com.pos.booklibrary.persistence.query.SearchBookQuery
import com.pos.booklibrary.models.Book
import com.pos.booklibrary.models.BookOrder
import com.pos.booklibrary.persistence.query.UpdateOrderQuery
import com.pos.booklibrary.services.BookAccessService
import com.pos.identity.security.jwt.JwtProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/book-library")
class BookController {
    @Autowired
    private lateinit var bookAccessService: BookAccessService

    @Operation(summary = "Get all books")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Retrieved book list"),
            ApiResponse(responseCode = "400", description = "Bad request parameters")
        ]
    )
    @GetMapping("/books")
    fun getAllBooks(@RequestParam params: Map<String, String>) =
        bookAccessService.getAllBooks(SearchBookQuery(params))

    @Operation(summary = "Get a single book")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Book retrieved"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @GetMapping("/books/{isbn}")
    fun getBook(@PathVariable isbn: String, @RequestParam verbose: Boolean?): ResponseEntity<EntityModel<BasicBook>> {
        val flag = verbose ?: true
        return bookAccessService.getBook(isbn, flag)
    }

    @Operation(summary = "Post a request to update book stocks")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "202", description = "Stocks updated"),
            ApiResponse(responseCode = "406", description = "Requested order could not be accepted")
        ]
    )
    @PostMapping("/orders")
    fun postBookOrder(@RequestBody orders: List<BookOrder>) = bookAccessService.postOrder(UpdateOrderQuery(orders))

    @Operation(summary = "Create a single book")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Book created"),
            ApiResponse(responseCode = "406", description = "Missing or invalid book data")
        ]
    )
    @PostMapping("/books")
    fun postBook(
        @RequestBody newBook: Book,
        @RequestHeader("Authorization") authHeader: String?
    ) = bookAccessService.postBook(newBook, JwtProvider.getToken(authHeader))

    @Operation(summary = "Update or create a single book")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Book updated"),
            ApiResponse(responseCode = "201", description = "Book created"),
            ApiResponse(responseCode = "406", description = "Missing required book data"),
            ApiResponse(responseCode = "409", description = "Error when inserting book data"),
        ]
    )
    @PutMapping("/books/{isbn}")
    fun putBook(
        @PathVariable isbn: String,
        @RequestBody newBook: Book,
        @RequestHeader("Authorization") authHeader: String?
    ) = bookAccessService.putBook(isbn, newBook, JwtProvider.getToken(authHeader))

    @Operation(summary = "Delete a single book")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Book deleted"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @DeleteMapping("/books/{isbn}")
    fun deleteBook(@PathVariable isbn: String, @RequestHeader("Authorization") authHeader: String?) =
        bookAccessService.deleteBook(isbn, JwtProvider.getToken(authHeader))

    @Operation(summary = "Get the book's author list")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Authors retrieved")
        ]
    )
    @GetMapping("/books/{isbn}/authors")
    fun getBookAuthors(@PathVariable isbn: String) = bookAccessService.getBookAuthors(isbn)

    @Operation(summary = "Get book author with specified index")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Author retrieved"),
            ApiResponse(responseCode = "404", description = "Author or book does not exist")
        ]
    )
    @GetMapping("/books/{isbn}/authors/{index}")
    fun getBookAuthor(@PathVariable isbn: String, @PathVariable index: Long) =
        bookAccessService.getBookAuthor(isbn, index)

    @Operation(
        summary = "Create or update the list of author IDs of a specified book. " +
                "The author's position in the array determines the index"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "202", description = "Book's author list created or updated"),
            ApiResponse(responseCode = "406", description = "Invalid author data"),
        ]
    )
    @PostMapping("/books/{isbn}/authors")
    fun postBookAuthors(
        @PathVariable isbn: String,
        @RequestBody bookAuthorIds: List<Long>,
        @RequestHeader("Authorization") authHeader: String?
    ) = bookAccessService.postBookAuthors(isbn, bookAuthorIds, JwtProvider.getToken(authHeader))

    @Operation(summary = "Delete author at specified index from a book")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "202", description = "Author deleted"),
            ApiResponse(responseCode = "404", description = "Book or author not found"),
        ]
    )
    @DeleteMapping("/books/{isbn}/authors/{index}")
    fun deleteBookAuthor(
        @PathVariable isbn: String,
        @PathVariable index: Long,
        @RequestHeader("Authorization") authHeader: String?
    ) = bookAccessService.deleteBookAuthor(isbn, index, JwtProvider.getToken(authHeader))

    @Operation(summary = "Delete all authors from a book, if any exist")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "202", description = "Authors deleted"),
            ApiResponse(responseCode = "404", description = "Book not found"),
        ]
    )
    @DeleteMapping("/books/{isbn}/authors")
    fun deleteBookAuthors(@PathVariable isbn: String, @RequestHeader("Authorization") authHeader: String?) =
        bookAccessService.deleteBookAuthors(isbn, JwtProvider.getToken(authHeader))
}