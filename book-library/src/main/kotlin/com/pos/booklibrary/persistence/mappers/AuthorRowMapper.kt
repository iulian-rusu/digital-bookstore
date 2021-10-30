package com.pos.booklibrary.persistence.mappers

import com.pos.booklibrary.models.Author
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class AuthorRowMapper : RowMapper<Author> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Author =
        Author(
            id = rs.getLong("author_id"),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name")
        )
}