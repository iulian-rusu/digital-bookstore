package com.pos.booklibrary.services

import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.interfaces.BookAccessInterface
import com.pos.booklibrary.persistence.BookAuthorRepository
import com.pos.booklibrary.persistence.BookRepository
import com.pos.booklibrary.models.Book
import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.models.BookAuthorId
import com.pos.booklibrary.views.BookAuthorModelAssembler
import com.pos.booklibrary.views.BookModelAssembler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.MissingRequestValueException

@Service
class BookAccessService : BookAccessInterface {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var bookAuthorRepository: BookAuthorRepository

    @Autowired
    private lateinit var bookAssembler: BookModelAssembler

    @Autowired
    private lateinit var bookAuthorAssembler: BookAuthorModelAssembler

    override fun getAllBooks(): CollectionModel<EntityModel<Book>> = bookRepository.findAll()
        .map(bookAssembler::toModel)
        .let { modelList ->
            CollectionModel.of(
                modelList,
                linkTo(methodOn(BookController::class.java).getAllBooks()).withSelfRel()
            )
        }

    override fun getBook(isbn: String): ResponseEntity<EntityModel<Book>> = bookRepository.findByIdOrNull(isbn)
        ?.let { ResponseEntity.ok(bookAssembler.toModel(it)) }
        ?: ResponseEntity.notFound().build()

    override fun postBook(newBook: Book): ResponseEntity<EntityModel<Book>> {
        return try {
            if (hasMissingFields(newBook)) {
                throw MissingRequestValueException("Missing required fields")
            }
            val entityModel = bookAssembler.toModel(bookRepository.save(newBook))
            ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun putBook(isbn: String, newBook: Book): ResponseEntity<EntityModel<Book>> {
        newBook.setIsbn(isbn)
        if (hasMissingFields(newBook)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
        return try {
            bookRepository.findByIdOrNull(isbn)?.let { existingBook ->
                // Update existing Book
                existingBook.apply {
                    setTitle(newBook.getTitle())
                    setGenre(newBook.getGenre())
                    setPublisher(newBook.getPublisher())
                    setPublishYear(newBook.getPublishYear())
                }
                bookRepository.save(existingBook)
                ResponseEntity.noContent().build()
            } ?: run {
                // Create a new Book
                newBook.setIsbn(isbn)
                val entityModel = bookAssembler.toModel(bookRepository.save(newBook))
                ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteBook(isbn: String): ResponseEntity<Unit> {
        return try {
            bookRepository.deleteById(isbn)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    override fun getBookAuthors(isbn: String): CollectionModel<EntityModel<BookAuthor>> =
        bookAuthorRepository.findBookAuthors(isbn)
            .map(bookAuthorAssembler::toModel)
            .let { modelList ->
                CollectionModel.of(
                    modelList,
                    linkTo(methodOn(BookController::class.java).getBookAuthors(isbn)).withSelfRel()
                )
            }

    override fun getBookAuthor(isbn: String, index: Long): ResponseEntity<EntityModel<BookAuthor>> {
        return try {
            bookAuthorRepository.findBookAuthorByIndex(isbn, index)?.let { bookAuthor ->
                ResponseEntity.ok(bookAuthorAssembler.toModel(bookAuthor))
            } ?: ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    override fun postBookAuthor(isbn: String, bookAuthor: BookAuthor): ResponseEntity<EntityModel<BookAuthor>> {
        return try {
            // Generate index that does not already exist
            val bookAuthors = bookAuthorRepository.findBookAuthors(isbn)
            var nextIndex = bookAuthors.count().toLong()
            while (bookAuthors.any { it.getAuthorIndex() == nextIndex }) {
                ++nextIndex
            }
            bookAuthor.setAuthorIndex(nextIndex)
            bookAuthor.setIsbn(isbn)
            if (hasMissingFields(bookAuthor)) {
                throw MissingRequestValueException("Missing required fields")
            }
            bookAuthorRepository.save(bookAuthor)
            val entityModel = bookAuthorAssembler.toModel(bookAuthor)
            ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun patchBookAuthors(
        isbn: String,
        bookAuthors: List<BookAuthor>
    ): ResponseEntity<CollectionModel<EntityModel<BookAuthor>>> {
        return try {
            val existingAuthors = bookAuthorRepository.findBookAuthors(isbn)
            var nextIndex = existingAuthors.count().toLong()
            bookAuthors.forEach { bookAuthor ->
                bookAuthor.setIsbn(isbn)
                // Check if index already has a BookAuthor
                if (existingAuthors.any { it.getAuthorIndex() == bookAuthor.getAuthorIndex() }) {
                    // Update existing BookAuthor
                    if (hasMissingFields(bookAuthor)) {
                        throw MissingRequestValueException("Missing required fields")
                    }
                    bookAuthorRepository.updateBookAuthor(
                        isbn,
                        bookAuthor.getAuthorId(),
                        bookAuthor.getAuthorIndex()
                    )
                } else {
                    // Generate next index and add new BookAuthor
                    bookAuthor.setAuthorIndex(nextIndex++)
                    bookAuthorRepository.save(bookAuthor)
                }
            }
            ResponseEntity.accepted().body(getBookAuthors(isbn))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun deleteBookAuthor(isbn: String, index: Long): ResponseEntity<Unit> {
        return try {
            bookAuthorRepository.findBookAuthorByIndex(isbn, index)?.let {
                val authorId = it.getAuthorId()
                bookAuthorRepository.deleteById(BookAuthorId(isbn, authorId))
                ResponseEntity.noContent().build()
            } ?: ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    private fun hasMissingFields(book: Book) = book.run {
        getIsbn().isEmpty() || getTitle().isEmpty() || getGenre().isEmpty() ||
                getPublisher().isEmpty() || getPublishYear() <= 0
    }

    private fun hasMissingFields(bookAuthor: BookAuthor) = bookAuthor.run {
        getIsbn().isEmpty() || getAuthorId() < 0 || getAuthorIndex() < 0
    }
}