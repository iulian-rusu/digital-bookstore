package com.pos.identity.models

data class User(
    val userId: Long,
    val username: String,
    val role: String
)
