package com.pos.identity.business.interfaces

import com.pos.identity.*

interface UserManagementInterface {
    fun authenticate(request: UserAuthenticationRequest): UserAuthenticationResponse
    fun register(request: UserRegistrationRequest): UserRegistrationResponse
    fun update(request: UserUpdateRequest): UserUpdateResponse
    fun delete(request: UserDeletionRequest): UserDeletionResponse
}