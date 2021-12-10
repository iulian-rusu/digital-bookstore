package com.pos.identity.services

import com.pos.identity.TokenDestructionRequest
import com.pos.identity.TokenDestructionResponse
import com.pos.identity.TokenValidationRequest
import com.pos.identity.TokenValidationResponse
import com.pos.identity.interfaces.JwtManagementInterface
import com.pos.identity.endpoints.ResponseStatus
import com.pos.identity.security.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JwtManagementService : GuardedScopeService(), JwtManagementInterface {
    @Autowired
    private lateinit var jwtProvider: JwtProvider

    override fun validate(request: TokenValidationRequest): TokenValidationResponse {
        val response = TokenValidationResponse()
        return guardedScope(
            action = {
                jwtProvider.getUserDetails(request.token).let { details ->
                    response.apply {
                        status = ResponseStatus.SUCCESS
                        sub = details.userId
                        role = details.role
                    }
                }
            },
            onError = {
                response.apply {
                    status = errorStatus(it)
                }
            }
        )
    }

    override fun destroy(request: TokenDestructionRequest): TokenDestructionResponse {
        val response = TokenDestructionResponse()
        return guardedScope(
            action = {
                jwtProvider.destroyToken(request.token)
                response.apply {
                    status = ResponseStatus.SUCCESS
                }
            },
            onError = {
                response.apply {
                    status = errorStatus(it)
                }
            }
        )
    }
}