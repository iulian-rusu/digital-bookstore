package com.pos.booklibrary.persistence

import com.pos.booklibrary.business.models.Author
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

interface AuthorRepository : CrudRepository<Author, Long> {
    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO authors(author_id, first_name, last_name) VALUES (:id, :firstName, :lastName) " +
                "ON DUPLICATE KEY UPDATE first_name=:firstName, last_name=:lastName",
        nativeQuery = true
    )
    fun saveWithId(
        @Param("id") id: Long,
        @Param("firstName") firstName: String,
        @Param("lastName") lastName: String
    )
}