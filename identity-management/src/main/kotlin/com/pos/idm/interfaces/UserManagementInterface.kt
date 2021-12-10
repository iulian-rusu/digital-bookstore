package com.pos.idm.interfaces

import com.pos.idm.*

interface UserManagementInterface {
    fun authenticate(request: AuthRequest): AuthResponse
    fun register(request: RegisterRequest): RegisterResponse
    fun update(request: UpdateRequest, authHeader: String): UpdateResponse
}