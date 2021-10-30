package com.pos.booklibrary.persistence.mappers

import com.pos.booklibrary.models.Book
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class BookRowMapper : RowMapper<Book> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Book =
        Book(
            isbn = rs.getString("isbn"),
            title = rs.getString("title"),
            publisher = rs.getString("publisher"),
            genre = rs.getString("genre"),
            publishYear = rs.getInt("publish_year")
        )
}