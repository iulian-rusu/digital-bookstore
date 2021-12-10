package com.pos.idm.persistence.mappers

import com.pos.idm.models.User
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