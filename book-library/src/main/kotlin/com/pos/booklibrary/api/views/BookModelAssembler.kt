package com.pos.booklibrary.api.views

import com.pos.booklibrary.api.controllers.BookController
import com.pos.booklibrary.api.requests.StockUpdateRequest
import com.pos.booklibrary.business.models.BasicBook
import com.pos.booklibrary.business.models.Book
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class BookModelAssembler : RepresentationModelAssembler<BasicBook, EntityModel<BasicBook>> {
    override fun toModel(book: BasicBook) = EntityModel.of(
        book,
        linkTo(methodOn(BookController::class.java).getBook(book.getIsbn(), null)).withSelfRel(),
        linkTo(methodOn(BookController::class.java).getBookAuthors(book.getIsbn())).withRel("get book authors"),
        linkTo(methodOn(BookController::class.java).getAllBooks(mapOf())).withRel("get all books"),
        linkTo(methodOn(BookController::class.java).postBook(Book(), null)).withRel("create book"),
        linkTo(methodOn(BookController::class.java).putBook(book.getIsbn(), Book(), null)).withRel("update book"),
        linkTo(methodOn(BookController::class.java).deleteBook(book.getIsbn(), null)).withRel("delete book"),
        linkTo(methodOn(BookController::class.java).postStockUpdate(StockUpdateRequest(listOf()), null))
            .withRel("update stocks"),
    )
}