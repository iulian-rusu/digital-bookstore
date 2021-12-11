package com.pos.identity.persistence.query

class UpdateUserQuery(
    val userId: Long,
    val passwordHash: String?,
    val role: String?
) : ParametrizedQuery() {

    init {
        params["userId"] = userId
        params["passwordHash"] = passwordHash
        params["role"] = role
    }

    override fun getSql(): String {
        val updateConditions = mutableListOf<String>()
        val sqlBuilder = StringBuilder("UPDATE users SET ")
        if (passwordHash != null)
            updateConditions.add("password_hash = :passwordHash")
        if (role != null)
            updateConditions.add("role = :role")
        if (updateConditions.isNotEmpty()) {
            sqlBuilder.append(updateConditions.joinToString(", "))
            sqlBuilder.append(" WHERE user_id = :userId")
        }
        return sqlBuilder.toString()
    }
}