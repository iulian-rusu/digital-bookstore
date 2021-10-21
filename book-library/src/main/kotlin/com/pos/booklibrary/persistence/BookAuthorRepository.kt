package com.pos.booklibrary.persistence

import com.pos.booklibrary.models.BookAuthor
import com.pos.booklibrary.models.BookAuthorId
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface BookAuthorRepository : CrudRepository<BookAuthor, BookAuthorId> {
    @Query(
        value = "SELECT * FROM book_authors " +
                "WHERE book_authors.isbn = :targetIsbn",
        nativeQuery = true
    )
    fun findBookAuthors(@Param("targetIsbn") isbn: String): MutableIterable<BookAuthor>

    @Query(
        value = "SELECT * FROM book_authors b " +
                "WHERE b.isbn = :targetIsbn AND b.author_index = :targetIndex " +
                "LIMIT 1",
        nativeQuery = true
    )
    fun fingBookAuthorByIndex(
        @Param("targetIsbn") isbn: String,
        @Param("targetIndex") index: Long
    ): BookAuthor
}