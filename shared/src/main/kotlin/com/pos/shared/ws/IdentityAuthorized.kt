package com.pos.shared.ws

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
abstract class IdentityAuthorized {
    @Autowired
    private lateinit var identityClient: IdentityClient

    protected fun <T> ifAuthorized(token: String?, role: String, action: () -> ResponseEntity<T>): ResponseEntity<T> {
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val response = identityClient.validateToken(token)
        if (response.role != role)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        return action()
    }
}