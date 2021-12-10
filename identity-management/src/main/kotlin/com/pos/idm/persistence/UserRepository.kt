package com.pos.idm.persistence

import com.pos.idm.models.User
import com.pos.idm.persistence.mappers.UserRowMapper
import com.pos.idm.security.Encoder
import com.pos.idm.security.UserRole
import org.springframework.stereotype.Repository
import com.pos.idm.*

@Repository
class UserRepository : RepositoryBase() {
    fun save(request: RegisterRequest): User {
        val passwordHash = Encoder.hashString(request.password)
        val params = mapOf(
            "username" to request.username,
            "passwordHash" to passwordHash,
            "role" to UserRole.USER
        )
        namedJdbcTemplate.update(
            """
                INSERT INTO users (username, password_hash, role) 
                VALUES (:username, BINARY :passwordHash, :role);
            """,
            params
        )

        return findOne(request.username)
    }

    fun findOne(username: String): User =
        jdbcTemplate.query(
            "SELECT * FROM users WHERE username = ?",
            UserRowMapper(), username
        ).first()

    fun findOne(userId: Long): User =
        jdbcTemplate.query(
            "SELECT * FROM users WHERE user_id = ?",
            UserRowMapper(), userId
        ).first()

    fun deleteOne(userId: Long) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId)
    }

    fun findPasswordHash(username: String) =
        jdbcTemplate.queryForObject(
            "SELECT password_hash FROM users WHERE username = ?",
            String::class.java,
            username
        )
}