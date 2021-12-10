package com.pos.idm.interfaces

import com.pos.idm.TokenDestructionRequest
import com.pos.idm.TokenDestructionResponse
import com.pos.idm.TokenValidationRequest
import com.pos.idm.TokenValidationResponse

interface JwtManagementInterface {
    fun validate(request: TokenValidationRequest): TokenValidationResponse
    fun destroy(request: TokenDestructionRequest): TokenDestructionResponse
}