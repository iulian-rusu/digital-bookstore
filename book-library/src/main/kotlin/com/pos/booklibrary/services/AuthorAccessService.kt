package com.pos.booklibrary.services

import com.pos.booklibrary.controllers.AuthorController
import com.pos.booklibrary.interfaces.AuthorAccessInterface
import com.pos.booklibrary.persistence.AuthorRepository
import com.pos.booklibrary.models.Author
import com.pos.booklibrary.views.AuthorModelAssembler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AuthorAccessService : AuthorAccessInterface {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var authorAssembler: AuthorModelAssembler

    override fun getAllAuthors(): CollectionModel<EntityModel<Author>> = authorRepository.findAll()
        .map(authorAssembler::toModel)
        .run {
            CollectionModel.of(
                this,
                linkTo(methodOn(AuthorController::class.java).getAllAuthors()).withSelfRel()
            )
        }

    override fun getAuthor(id: Long): ResponseEntity<EntityModel<Author>> = authorRepository.findById(id)
        .run {
            if (isPresent)
                ResponseEntity.ok(authorAssembler.toModel(get()))
            else
                ResponseEntity.notFound().build()
        }

    override fun postAuthor(newAuthor: Author): ResponseEntity<EntityModel<Author>> {
        return try {
            newAuthor.setId(authorRepository.count())
            val entityModel = authorAssembler.toModel(authorRepository.save(newAuthor))
            ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun putAuthor(id: Long, newAuthor: Author): ResponseEntity<EntityModel<Author>> {
        // Check if request body has all valid fields
        if (newAuthor.getFirstName().isEmpty() || newAuthor.getLastName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
        try {
            val existentAuthor = authorRepository.findById(id)
            return if (existentAuthor.isPresent) {
                // Replace existing Author
                existentAuthor.get().apply {
                    setFirstName(newAuthor.getFirstName())
                    setLasttName(newAuthor.getLastName())
                    authorRepository.save(this)
                }
                ResponseEntity.noContent().build()
            } else {
                // Create a new Author
                newAuthor.setId(id)
                val entityModel = authorAssembler.toModel(authorRepository.save(newAuthor))
                ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteAuthor(id: Long): ResponseEntity<Unit> {
        return try {
            authorRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }
}