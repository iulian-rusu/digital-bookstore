package com.pos.booklibrary.persistence

import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.models.BookAuthorId
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

interface BookAuthorRepository : CrudRepository<BookAuthor, BookAuthorId> {
    @Query(
        value = "SELECT * FROM book_authors WHERE book_authors.isbn = :targetIsbn",
        nativeQuery = true
    )
    fun findBookAuthors(@Param("targetIsbn") isbn: String): MutableIterable<BookAuthor>

    @Query(
        value = "SELECT * FROM book_authors b WHERE b.isbn = :targetIsbn AND b.author_index = :targetIndex LIMIT 1",
        nativeQuery = true
    )
    fun findBookAuthorByIndex(
        @Param("targetIsbn") isbn: String,
        @Param("targetIndex") index: Long
    ): BookAuthor?

    @Transactional
    @Modifying
    @Query(
        value = "UPDATE book_authors SET author_id = :newId WHERE author_index = :targetIndex AND isbn = :targetIsbn",
        nativeQuery = true
    )
    fun updateBookAuthor(
        @Param("targetIsbn") isbn: String,
        @Param("newId") authorId: Long,
        @Param("targetIndex") authorIndex: Long
    )
}