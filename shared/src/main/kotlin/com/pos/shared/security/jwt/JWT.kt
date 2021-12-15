package com.pos.shared.security.jwt

object JWT {
    fun getToken(authHeader: String?): String? {
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.split(" ")[1]
        } else null
    }
}