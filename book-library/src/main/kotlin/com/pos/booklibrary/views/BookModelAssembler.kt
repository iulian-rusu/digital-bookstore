package com.pos.booklibrary.views

import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.models.BasicBook
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
        linkTo(methodOn(BookController::class.java).getBookAuthors(book.getIsbn())).withRel("authors"),
        linkTo(methodOn(BookController::class.java).getAllBooks(mapOf())).withRel("books")
    )
}