package com.pos.booklibrary.views

import com.pos.booklibrary.controllers.BookController
import com.pos.booklibrary.models.BookAuthor
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component

@Component
class BookAuthorModelAssembler : RepresentationModelAssembler<BookAuthor, EntityModel<BookAuthor>> {
    override fun toModel(author: BookAuthor) = EntityModel.of(
        author,
        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController::class.java)
            .getBookAuthor(author.getIsbn(), author.getAuthorIndex())).withSelfRel(),
        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController::class.java)
            .getBookAuthors(author.getIsbn())).withRel("bookAuthors")
    )
}