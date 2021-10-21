package com.pos.booklibrary.views

import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.models.Book
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class BookModelAssembler : RepresentationModelAssembler<Book, EntityModel<Book>> {
    override fun toModel(book: Book) = EntityModel.of(
        book,
        linkTo(methodOn(BookController::class.java).getBook(book.getIsbn())).withSelfRel(),
        linkTo(methodOn(BookController::class.java).getAllBooks()).withRel("books")
    )
}