package com.pos.booklibrary.services

import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.persistence.AuthorRepository
import com.pos.booklibrary.interfaces.BookAccessInterface
import com.pos.booklibrary.persistence.BookAuthorRepository
import com.pos.booklibrary.persistence.BookRepository
import com.pos.booklibrary.models.Book
import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.models.BookAuthorId
import com.pos.booklibrary.views.BookAuthorModelAssembler
import com.pos.booklibrary.views.BookModelAssembler
import javassist.NotFoundException
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
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var bookAuthorRepository: BookAuthorRepository

    @Autowired
    private lateinit var bookAssembler: BookModelAssembler

    @Autowired
    private lateinit var bookAuthorAssembler: BookAuthorModelAssembler

    override fun getAllBooks(): CollectionModel<EntityModel<Book>> = bookRepository.findAll()
        .map(bookAssembler::toModel)
        .run {
            CollectionModel.of(
                this,
                linkTo(methodOn(BookController::class.java).getAllBooks()).withSelfRel()
            )
        }

    override fun getBook(isbn: String): ResponseEntity<EntityModel<Book>> = bookRepository.findById(isbn)
        .run {
            if (isPresent)
                ResponseEntity.ok(bookAssembler.toModel(get()))
            else
                ResponseEntity.notFound().build()
        }

    override fun postBook(newBook: Book): ResponseEntity<EntityModel<Book>> {
        return try {
            if (!validateFields(newBook)) {
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
        // Check if request body has all valid fields
        if (!validateFields(newBook)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
        try {
            val existentBook = bookRepository.findById(isbn)
            return if (existentBook.isPresent) {
                // Replace existing Book
                existentBook.get().apply {
                    setTitle(newBook.getTitle())
                    setGenre(newBook.getGenre())
                    setPublisher(newBook.getPublisher())
                    setPublishYear(newBook.getPublishYear())
                    bookRepository.save(this)
                }
                ResponseEntity.noContent().build()
            } else {
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
            .run {
                CollectionModel.of(
                    this,
                    linkTo(methodOn(BookController::class.java).getBookAuthors(isbn)).withSelfRel()
                )
            }

    override fun getBookAuthor(isbn: String, index: Long): ResponseEntity<EntityModel<BookAuthor>> {
        return try {
            ResponseEntity.ok(bookAuthorAssembler.toModel(bookAuthorRepository.fingBookAuthorByIndex(isbn, index)))
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    override fun putBookAuthor(isbn: String, index: Long, authorIndex: Long): ResponseEntity<EntityModel<BookAuthor>> {
        return try {
            val bookAuthor = BookAuthor(isbn, authorIndex, index)
            if (!validateFields(bookAuthor)) {
                throw MissingRequestValueException("Missing required fields")
            }
            return authorRepository.findByIdOrNull(bookAuthor.getAuthorId())?.run {
                bookAuthorRepository.save(bookAuthor)
                val entityModel = bookAuthorAssembler.toModel(bookAuthor)
                ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun patchBookAuthors(
        isbn: String,
        bookAuthors: List<BookAuthor>
    ): ResponseEntity<CollectionModel<EntityModel<BookAuthor>>> {
        return try {
            bookAuthors.map { bookAuthor ->
                bookAuthor.setIsbn(isbn)
                if (!validateFields(bookAuthor)) {
                    throw MissingRequestValueException("Missing required fields")
                }
                authorRepository.findByIdOrNull(bookAuthor.getAuthorId())?.let {
                    bookAuthorRepository.save(bookAuthor)
                } ?: throw NotFoundException("Author not found")
            }
            ResponseEntity.accepted().body(getBookAuthors(isbn))
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun deleteBookAuthors(isbn: String, authorId: Long): ResponseEntity<Unit> {
        return try {
            bookAuthorRepository.deleteById(BookAuthorId(isbn, authorId))
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    private fun validateFields(book: Book) = book.run {
        getIsbn().isEmpty() || getTitle().isEmpty() || getGenre().isEmpty() ||
                getPublisher().isEmpty() || getPublishYear() <= 0
    }.not()

    private fun validateFields(bookAuthor: BookAuthor) = bookAuthor.run {
        getIsbn().isEmpty() || getAuthorId() < 0 || getAuthorIndex() < 0
    }.not()
}