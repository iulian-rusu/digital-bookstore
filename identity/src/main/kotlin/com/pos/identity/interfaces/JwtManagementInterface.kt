package com.pos.identity.interfaces

import com.pos.identity.TokenDestructionRequest
import com.pos.identity.TokenDestructionResponse
import com.pos.identity.TokenValidationRequest
import com.pos.identity.TokenValidationResponse

interface JwtManagementInterface {
    fun validate(request: TokenValidationRequest): TokenValidationResponse
    fun destroy(request: TokenDestructionRequest): TokenDestructionResponse
}