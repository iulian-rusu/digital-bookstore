package com.pos.booklibrary.business.services

import com.pos.booklibrary.api.controllers.AuthorController
import com.pos.booklibrary.api.views.AuthorModelAssembler
import com.pos.booklibrary.business.interfaces.AuthorAccessInterface
import com.pos.booklibrary.business.models.Author
import com.pos.booklibrary.persistence.AuthorRepository
import com.pos.booklibrary.persistence.GenericQueryRepository
import com.pos.booklibrary.persistence.mappers.AuthorRowMapper
import com.pos.booklibrary.persistence.query.SearchAuthorQuery
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
class AuthorAccessService : IdentityAuthorized(), AuthorAccessInterface {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var customQueryRepository: GenericQueryRepository

    @Autowired
    private lateinit var authorAssembler: AuthorModelAssembler

    private val logger = LoggerFactory.getLogger(AuthorAccessService::class.java)

    override fun getAllAuthors(query: SearchAuthorQuery): ResponseEntity<CollectionModel<EntityModel<Author>>> =
        try {
            customQueryRepository.find(query, AuthorRowMapper())
                .map(authorAssembler::toModel)
                .let { modelList ->
                    ResponseEntity.ok(
                        CollectionModel.of(
                            modelList,
                            linkTo(methodOn(AuthorController::class.java).getAllAuthors(mapOf())).withSelfRel()
                        )
                    )
                }
        } catch (e: Exception) {
            logger.error("getAllAuthors(): $e")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

    override fun getAuthor(id: Long): ResponseEntity<EntityModel<Author>> = authorRepository.findByIdOrNull(id)
        ?.let { ResponseEntity.ok(authorAssembler.toModel(it)) }
        ?: ResponseEntity.notFound().build()

    override fun postAuthor(newAuthor: Author, token: String?): ResponseEntity<EntityModel<Author>> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                val entityModel = authorAssembler.toModel(authorRepository.save(newAuthor))
                ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            }
        } catch (e: Exception) {
            logger.error("postAuthor(firstName=${newAuthor.getFirstName()}): $e")
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun putAuthor(id: Long, newAuthor: Author, token: String?): ResponseEntity<EntityModel<Author>> {
        newAuthor.setId(id)
        if (hasMissingFields(newAuthor)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                authorRepository.findByIdOrNull(id)?.let {
                    // Update existing Author
                    newAuthor.run {
                        authorRepository.saveWithId(id, getFirstName(), getLastName())
                    }
                    ResponseEntity.noContent().build()
                } ?: run {
                    // Create a new Author
                    val entityModel = authorAssembler.toModel(newAuthor.run {
                        authorRepository.saveWithId(id, getFirstName(), getLastName())
                        this
                    })
                    ResponseEntity
                        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                        .body(entityModel)
                }
            }
        } catch (e: Exception) {
            logger.error("putAuthor(id=$id): $e")
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteAuthor(id: Long, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorizedAs(UserRole.MANAGER, token) {
                authorRepository.deleteById(id)
                ResponseEntity.noContent().build()
            }
        } catch (e: Exception) {
            logger.error("deleteAuthor(id=$id): $e")
            ResponseEntity.notFound().build()
        }
    }

    private fun hasMissingFields(author: Author) = author.run {
        getId() < 0 || getFirstName().isEmpty() || getLastName().isEmpty()
    }
}