package com.pos.booklibrary.persistence

import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.models.BookAuthorIndex
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

interface BookAuthorRepository : CrudRepository<BookAuthor, BookAuthorIndex> {
    @Query(
        value = "SELECT * FROM book_authors WHERE isbn = :targetIsbn",
        nativeQuery = true
    )
    fun findBookAuthors(@Param("targetIsbn") isbn: String): MutableIterable<BookAuthor>

    @Query(
        value = "SELECT * FROM book_authors WHERE isbn = :targetIsbn AND author_index = :targetIndex LIMIT 1",
        nativeQuery = true
    )
    fun findBookAuthorByIndex(
        @Param("targetIsbn") isbn: String,
        @Param("targetIndex") index: Long
    ): BookAuthor?

    @Transactional
    @Modifying
    @Query(
        value = "DELETE FROM book_authors WHERE isbn = :targetIsbn",
        nativeQuery = true
    )
    fun deleteBookAuthors(@Param("targetIsbn") isbn: String)

    @Transactional
    @Modifying
    @Query(
        value = "DELETE FROM book_authors WHERE isbn = :targetIsbn AND author_index = :targetIndex",
        nativeQuery = true
    )
    fun deleteBookAuthorByIndex(
        @Param("targetIsbn") isbn: String,
        @Param("targetIndex") index: Long
    )
}