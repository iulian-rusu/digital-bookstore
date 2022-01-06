package com.pos.shared.proxy.api.requests

data class RestUserUpdateRequest(
    val token: String,
    val userId: Long,
    val password: String?,
    val role: String?
)
