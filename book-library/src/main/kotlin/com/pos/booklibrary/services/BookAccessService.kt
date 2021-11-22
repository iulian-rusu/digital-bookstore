package com.pos.booklibrary.services

import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.persistence.query.SearchBookQuery
import com.pos.booklibrary.interfaces.BookAccessInterface
import com.pos.booklibrary.models.*
import com.pos.booklibrary.persistence.BookAuthorRepository
import com.pos.booklibrary.persistence.BookRepository
import com.pos.booklibrary.persistence.GenericQueryRepository
import com.pos.booklibrary.persistence.mappers.BookRowMapper
import com.pos.booklibrary.persistence.query.UpdateOrderQuery
import com.pos.booklibrary.views.BookAuthorModelAssembler
import com.pos.booklibrary.views.BookModelAssembler
import org.slf4j.LoggerFactory
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
    private lateinit var customQueryRepository: GenericQueryRepository

    @Autowired
    private lateinit var bookAssembler: BookModelAssembler

    @Autowired
    private lateinit var bookAuthorAssembler: BookAuthorModelAssembler

    private val logger = LoggerFactory.getLogger(BookAccessService::class.java)

    override fun getAllBooks(query: SearchBookQuery): ResponseEntity<CollectionModel<EntityModel<BasicBook>>> =
        try {
            customQueryRepository.find(query, BookRowMapper())
                .map(bookAssembler::toModel)
                .let { modelList ->
                    ResponseEntity.ok(
                        CollectionModel.of(
                            modelList,
                            linkTo(methodOn(BookController::class.java).getAllBooks(emptyMap())).withSelfRel()
                        )
                    )
                }
        } catch (e: Exception) {
            logger.error("getAllBooks(): $e")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    override fun getBook(isbn: String, verbose: Boolean): ResponseEntity<EntityModel<BasicBook>> =
        bookRepository.findByIdOrNull(isbn)
            ?.let {
                val model = if (verbose) it else BriefBook(it)
                ResponseEntity.ok(bookAssembler.toModel(model))
            } ?: ResponseEntity.notFound().build()

    override fun postOrder(query: UpdateOrderQuery): ResponseEntity<Unit> {
        return try {
            customQueryRepository.execute(query)
            ResponseEntity.accepted().build()
        } catch (e: Exception) {
            logger.error("postOrder(): $e")
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun postBook(newBook: Book): ResponseEntity<EntityModel<BasicBook>> {
        return try {
            if (hasMissingFields(newBook)) {
                throw MissingRequestValueException("Missing required fields")
            }
            val entityModel = bookAssembler.toModel(bookRepository.save(newBook))
            ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel)
        } catch (e: Exception) {
            logger.error("postBook(isbn=${newBook.getIsbn()}): $e")
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun putBook(isbn: String, newBook: Book): ResponseEntity<EntityModel<BasicBook>> {
        newBook.setIsbn(isbn)
        if (hasMissingFields(newBook)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
        return try {
            bookRepository.findByIdOrNull(isbn)?.let {
                // Update existing Book
                bookRepository.save(newBook)
                ResponseEntity.noContent().build()
            } ?: run {
                // Create a new Book
                val entityModel = bookAssembler.toModel(bookRepository.save(newBook))
                ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            }
        } catch (e: Exception) {
            logger.error("putBook(isbn=$isbn): $e")
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteBook(isbn: String): ResponseEntity<Unit> {
        return try {
            bookRepository.deleteById(isbn)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            logger.error("deleteBook(isbn=$isbn): $e")
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
            logger.error("getBookAuthor(isbn=$isbn, index=$index): $e")
            ResponseEntity.notFound().build()
        }
    }

    override fun postBookAuthors(
        isbn: String,
        bookAuthorIds: List<Long>
    ): ResponseEntity<CollectionModel<EntityModel<BookAuthor>>> {
        return try {
            bookAuthorIds.mapIndexed { index, bookAuthorId ->
                BookAuthor(isbn, index.toLong(), bookAuthorId)
            }.also { newAuthors ->
                bookAuthorRepository.saveAll(newAuthors)
            }
            ResponseEntity.accepted().body(getBookAuthors(isbn))
        } catch (e: Exception) {
            logger.error("postBookAuthors(isbn=$isbn): $e")
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun deleteBookAuthor(isbn: String, index: Long): ResponseEntity<Unit> {
        return try {
            bookAuthorRepository.deleteBookAuthorByIndex(isbn, index)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            logger.error("deleteBookAuthor(isbn=$isbn, index=$index): $e")
            ResponseEntity.notFound().build()
        }
    }

    override fun deleteBookAuthors(isbn: String): ResponseEntity<Unit> {
        return try {
            bookAuthorRepository.deleteBookAuthors(isbn)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            logger.error("deleteBookAuthors(isbn=$isbn): $e")
            ResponseEntity.notFound().build()
        }
    }

    private fun hasMissingFields(book: Book) = book.run {
        getIsbn().isEmpty() || getTitle().isEmpty() || getGenre().isEmpty() ||
                getPublisher().isEmpty() || getPublishYear() <= 0
    }
}