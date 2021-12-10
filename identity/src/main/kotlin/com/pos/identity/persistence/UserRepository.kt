package com.pos.identity.persistence

import com.pos.identity.models.User
import com.pos.identity.persistence.mappers.UserRowMapper
import com.pos.identity.security.Encoder
import com.pos.identity.security.UserRole
import org.springframework.stereotype.Repository
import com.pos.identity.*

@Repository
class UserRepository : RepositoryBase() {
    fun save(request: UserRegistrationRequest): User {
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

    fun deleteOne(request: UserDeletionRequest) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", request.userId)
    }

    fun findPasswordHash(username: String) =
        jdbcTemplate.queryForObject(
            "SELECT password_hash FROM users WHERE username = ?",
            String::class.java,
            username
        )
}