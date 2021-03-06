package com.pos.booklibrary.api.views

import com.pos.booklibrary.api.controllers.AuthorController
import com.pos.booklibrary.business.models.Author
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class AuthorModelAssembler : RepresentationModelAssembler<Author, EntityModel<Author>> {
    override fun toModel(author: Author) = EntityModel.of(
        author,
        linkTo(methodOn(AuthorController::class.java).getAuthor(author.getId())).withSelfRel(),
        linkTo(methodOn(AuthorController::class.java).getAllAuthors(mapOf())).withRel("get all authors"),
        linkTo(methodOn(AuthorController::class.java).postAuthor(author, null)).withRel("create author"),
        linkTo(methodOn(AuthorController::class.java).putAuthor(author.getId(), author, null)).withRel("update author"),
        linkTo(methodOn(AuthorController::class.java).deleteAuthor(author.getId(), null)).withRel("delete author")
    )
}