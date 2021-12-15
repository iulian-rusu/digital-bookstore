package com.pos.shared.security

object UserRole {
    val ROLES = listOf(
        "ROLE_USER",
        "ROLE_MANAGER",
        "ROLE_ADMIN"
    )
    val USER = ROLES[0]
    val MANAGER = ROLES[1]
    val ADMIN = ROLES[2]
}