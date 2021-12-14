package com.pos.identity.services

import com.pos.identity.TokenDestructionRequest
import com.pos.identity.TokenDestructionResponse
import com.pos.identity.TokenValidationRequest
import com.pos.identity.TokenValidationResponse
import com.pos.identity.interfaces.JwtManagementInterface
import com.pos.identity.security.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JwtManagementService : GuardedSoapScope(JwtManagementService::class.java), JwtManagementInterface {
    @Autowired
    private lateinit var jwtProvider: JwtProvider

    override fun validate(request: TokenValidationRequest): TokenValidationResponse {
        val response = TokenValidationResponse()
        guardedScope {
            jwtProvider.getUser(request.token).let { details ->
                response.apply {
                    sub = details.userId
                    role = details.role
                }
            }
        }
        return response
    }

    override fun destroy(request: TokenDestructionRequest): TokenDestructionResponse {
        val response = TokenDestructionResponse()
        guardedScope {
            JwtProvider.destroyToken(request.token)
            response.token = request.token
        }
        return response
    }
}