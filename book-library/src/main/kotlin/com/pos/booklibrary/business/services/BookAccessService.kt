package com.pos.booklibrary.business.services

import com.pos.booklibrary.api.controllers.BookController
import com.pos.booklibrary.api.requests.StockUpdateRequest
import com.pos.booklibrary.api.views.BookAuthorModelAssembler
import com.pos.booklibrary.api.views.BookModelAssembler
import com.pos.booklibrary.business.models.BasicBook
import com.pos.booklibrary.business.models.Book
import com.pos.booklibrary.business.models.BookAuthor
import com.pos.booklibrary.business.models.BriefBook
import com.pos.booklibrary.business.services.interfaces.BookAccessInterface
import com.pos.booklibrary.persistence.BookAuthorRepository
import com.pos.booklibrary.persistence.BookRepository
import com.pos.booklibrary.persistence.GenericQueryRepository
import com.pos.booklibrary.persistence.mappers.BookRowMapper
import com.pos.booklibrary.persistence.query.SearchBookListQuery
import com.pos.booklibrary.persistence.query.SearchBookQuery
import com.pos.booklibrary.persistence.query.UpdateBookStockQuery
import com.pos.shared.identity.IdentityAuthorized
import com.pos.shared.security.UserRole
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

@Service
class BookAccessService : IdentityAuthorized(), BookAccessInterface {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var bookAuthorRepository: BookAuthorRepository

    @Autowired
    private lateinit var genericQueryRepository: GenericQueryRepository

    @Autowired
    private lateinit var bookAssembler: BookModelAssembler

    @Autowired
    private lateinit var bookAuthorAssembler: BookAuthorModelAssembler

    private val logger = LoggerFactory.getLogger(BookAccessService::class.java)

    override fun getAllBooks(query: SearchBookQuery): ResponseEntity<CollectionModel<EntityModel<BasicBook>>> {
        return try {
            genericQueryRepository.find(query, BookRowMapper())
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
    }

    override fun getBook(isbn: String, verbose: Boolean): ResponseEntity<EntityModel<BasicBook>> =
        bookRepository.findByIdOrNull(isbn)
            ?.let {
                val model = if (verbose) it else BriefBook(it)
                ResponseEntity.ok(bookAssembler.toModel(model))
            } ?: ResponseEntity.notFound().build()

    override fun postBook(newBook: Book, token: String?): ResponseEntity<EntityModel<BasicBook>> {
        if (hasMissingFields(newBook))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()

        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                val entityModel = bookAssembler.toModel(bookRepository.save(newBook))
                ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            }
        } catch (e: Exception) {
            logger.error("postBook(isbn=${newBook.getIsbn()}): $e")
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun putBook(isbn: String, newBook: Book, token: String?): ResponseEntity<EntityModel<BasicBook>> {
        newBook.setIsbn(isbn)
        if (hasMissingFields(newBook))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()

        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
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
            }
        } catch (e: Exception) {
            logger.error("putBook(isbn=$isbn): $e")
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteBook(isbn: String, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                bookRepository.deleteById(isbn)
                ResponseEntity.noContent().build()
            }
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
        bookAuthorIds: List<Long>,
        token: String?
    ): ResponseEntity<CollectionModel<EntityModel<BookAuthor>>> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                bookAuthorIds.mapIndexed { index, bookAuthorId ->
                    BookAuthor(isbn, index.toLong(), bookAuthorId)
                }.also { newAuthors ->
                    bookAuthorRepository.saveAll(newAuthors)
                }
                ResponseEntity.accepted().body(getBookAuthors(isbn))
            }
        } catch (e: Exception) {
            logger.error("postBookAuthors(isbn=$isbn): $e")
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun deleteBookAuthor(isbn: String, index: Long, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                bookAuthorRepository.deleteBookAuthorByIndex(isbn, index)
                ResponseEntity.noContent().build()
            }
        } catch (e: Exception) {
            logger.error("deleteBookAuthor(isbn=$isbn, index=$index): $e")
            ResponseEntity.notFound().build()
        }
    }

    override fun deleteBookAuthors(isbn: String, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                bookAuthorRepository.deleteBookAuthors(isbn)
                ResponseEntity.noContent().build()
            }
        } catch (e: Exception) {
            logger.error("deleteBookAuthors(isbn=$isbn): $e")
            ResponseEntity.notFound().build()
        }
    }

    override fun postStockUpdate(
        request: StockUpdateRequest,
        token: String?
    ): ResponseEntity<CollectionModel<EntityModel<BasicBook>>> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                genericQueryRepository.execute(UpdateBookStockQuery(request))
                val isbnList = request.items.map { it.isbn }
                genericQueryRepository.find(SearchBookListQuery(isbnList), BookRowMapper())
                    .map(bookAssembler::toModel)
                    .let { modelList ->
                        ResponseEntity.ok(
                            CollectionModel.of(
                                modelList,
                                linkTo(methodOn(BookController::class.java).getAllBooks(emptyMap())).withSelfRel()
                            )
                        )
                    }
            }
        } catch (e: Exception) {
            logger.error("getAllBooks(): $e")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    private fun hasMissingFields(book: Book) = book.run {
        getIsbn().isEmpty() || getTitle().isEmpty() || getGenre().isEmpty() ||
                getPublisher().isEmpty() || getPublishYear() <= 0
    }
}