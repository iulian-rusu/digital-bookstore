package com.pos.booklibrary.interfaces

import com.pos.booklibrary.persistence.query.AuthorSearchQuery
import com.pos.booklibrary.models.Author
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface AuthorAccessInterface {
    fun getAllAuthors(query: AuthorSearchQuery): CollectionModel<EntityModel<Author>>
    fun getAuthor(id: Long): ResponseEntity<EntityModel<Author>>
    fun postAuthor(newAuthor: Author): ResponseEntity<EntityModel<Author>>
    fun putAuthor(id: Long, newAuthor: Author): ResponseEntity<EntityModel<Author>>
    fun deleteAuthor(id: Long): ResponseEntity<Unit>
}