package com.pos.shared.proxy.api.requests

data class RestUserAuthenticationRequest(
    val username: String,
    val password: String
)
