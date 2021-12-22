package com.pos.booklibrary.business.services.interfaces

import com.pos.booklibrary.business.models.Author
import com.pos.booklibrary.persistence.query.SearchAuthorQuery
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface AuthorAccessInterface {
    fun getAllAuthors(query: SearchAuthorQuery): ResponseEntity<CollectionModel<EntityModel<Author>>>
    fun getAuthor(id: Long): ResponseEntity<EntityModel<Author>>
    fun postAuthor(newAuthor: Author, token: String?): ResponseEntity<EntityModel<Author>>
    fun putAuthor(id: Long, newAuthor: Author, token: String?): ResponseEntity<EntityModel<Author>>
    fun deleteAuthor(id: Long, token: String?): ResponseEntity<Unit>
}