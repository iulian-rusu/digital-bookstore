package com.pos.booklibrary.views

import com.pos.booklibrary.controllers.AuthorController
import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.models.BookAuthor
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class BookAuthorModelAssembler : RepresentationModelAssembler<BookAuthor, EntityModel<BookAuthor>> {
    override fun toModel(author: BookAuthor) = EntityModel.of(
        author,
        linkTo(methodOn(BookController::class.java).getBookAuthor(author.getIsbn(), author.getAuthorIndex())).withSelfRel(),
        linkTo(methodOn(BookController::class.java).getBook(author.getIsbn())).withRel("book"),
        linkTo(methodOn(AuthorController::class.java).getAuthor(author.getAuthorId())).withRel("author"),
    )
}