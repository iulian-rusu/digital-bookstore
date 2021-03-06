package com.pos.booklibrary.persistence.mappers

import com.pos.booklibrary.business.models.Book
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class BookRowMapper : RowMapper<Book> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
        Book(
            isbn = rs.getString("isbn"),
            title = rs.getString("title"),
            publisher = rs.getString("publisher"),
            publishYear = rs.getInt("publish_year"),
            genre = rs.getString("genre"),
            price = rs.getInt("price"),
            stock = rs.getInt("stock")
        )
}