package com.pos.booklibrary.controllers

import com.pos.booklibrary.models.Book
import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.models.BookAuthorId
import com.pos.booklibrary.services.BookAccessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/book-library")
class BookController {
    @Autowired
    private lateinit var bookAccessService: BookAccessService

    @GetMapping("/books")
    fun getAllBooks() = bookAccessService.getAllBooks()

    @GetMapping("/books/{isbn}")
    fun getBook(@PathVariable isbn: String) = bookAccessService.getBook(isbn)

    @PostMapping("/books")
    fun postBook(@RequestBody newBook: Book) = bookAccessService.postBook(newBook)

    @PutMapping("/books/{isbn}")
    fun putBook(@PathVariable isbn: String, @RequestBody newBook: Book) = bookAccessService.putBook(isbn, newBook)

    @DeleteMapping("/books/{isbn}")
    fun deleteBook(@PathVariable isbn: String) = bookAccessService.deleteBook(isbn)

    @GetMapping("/books/{isbn}/authors")
    fun getBookAuthors(@PathVariable isbn: String) = bookAccessService.getBookAuthors(isbn)

    @GetMapping("/books/{isbn}/authors/{index}")
    fun getBookAuthor(@PathVariable isbn: String, @PathVariable index: Long) =
        bookAccessService.getBookAuthor(isbn, index)

    @PutMapping("/books/{isbn}/authors/{index}")
    fun putBookAuthor(@PathVariable isbn: String, @PathVariable index: Long, @RequestBody authorId: BookAuthorId) =
        bookAccessService.putBookAuthor(isbn, index, authorId.authorId)

    @PatchMapping("/books/{isbn}/authors")
    fun patchBookAuthors(@PathVariable isbn: String, @RequestBody bookAuthors: List<BookAuthor>) =
        bookAccessService.patchBookAuthors(isbn, bookAuthors)

    @DeleteMapping("/books/{isbn}/authors/{index}")
    fun deleteBookAuthors(@PathVariable isbn: String, @PathVariable index: Long) =
        bookAccessService.deleteBookAuthors(isbn, index)
}