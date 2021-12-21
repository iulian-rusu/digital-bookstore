package com.pos.identity.business.models

data class User(
    val userId: Long,
    val username: String,
    val role: String
)
