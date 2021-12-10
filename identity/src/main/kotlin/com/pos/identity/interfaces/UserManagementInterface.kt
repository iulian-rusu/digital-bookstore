package com.pos.identity.interfaces

import com.pos.identity.*

interface UserManagementInterface {
    fun authenticate(request: UserAuthenticationRequest): UserAuthenticationResponse
    fun register(request: UserRegistrationRequest): UserRegistrationResponse
    fun update(request: UserUpdateRequest, authHeader: String): UserUpdateResponse
    fun delete(request: UserDeletionRequest, authHeader: String): UserDeletionResponse
}