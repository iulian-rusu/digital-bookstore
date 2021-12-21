package com.pos.shared.security

object UserRole {
    val VALUES = listOf(
        "ROLE_USER",
        "ROLE_MANAGER",
        "ROLE_ADMIN"
    )
    val USER = VALUES[0]
    val MANAGER = VALUES[1]
    val ADMIN = VALUES[2]
}