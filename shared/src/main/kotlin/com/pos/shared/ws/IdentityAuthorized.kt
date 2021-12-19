package com.pos.shared.ws

import com.pos.shared.TokenValidationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
abstract class IdentityAuthorized {
    @Autowired
    private lateinit var identityClient: IdentityClient

    protected fun getTokenFor(username: String, password: String): String {
        return identityClient.authenticate(username, password).token
    }

    protected fun <T> ifAuthorized(token: String?, role: String, action: () -> ResponseEntity<T>): ResponseEntity<T> {
        return withAuthorization(
            token = token,
            authorizer = { response -> response.role == role },
            action = action
        )
    }

    protected fun <T> ifAuthorized(token: String?, sub: Long, action: () -> ResponseEntity<T>): ResponseEntity<T> {
        return withAuthorization(
            token = token,
            authorizer = { response -> response.sub == sub },
            action = action
        )
    }

    protected fun <T> withAuthorization(
        token: String?,
        authorizer: (TokenValidationResponse) -> Boolean,
        action: () -> ResponseEntity<T>
    ): ResponseEntity<T> {
        if (token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        try {
            val response = identityClient.validateToken(token)
            if (!authorizer(response))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return action()
    }
}