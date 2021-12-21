package com.pos.identity.persistence.mappers

import com.pos.identity.business.models.User
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class UserRowMapper : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
        User(
            userId = rs.getLong("user_id"),
            username = rs.getString("username"),
            role = rs.getString("role")
        )
}