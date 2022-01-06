package com.pos.shared.proxy.api.responses

data class RestUserAuthenticationResponse(
    val token: String,
    val userId: Long,
    val role: String
)
