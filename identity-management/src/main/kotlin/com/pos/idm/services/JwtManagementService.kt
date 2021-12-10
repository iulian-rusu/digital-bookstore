package com.pos.idm.services

import com.pos.idm.TokenDestructionRequest
import com.pos.idm.TokenDestructionResponse
import com.pos.idm.TokenValidationRequest
import com.pos.idm.TokenValidationResponse
import com.pos.idm.interfaces.JwtManagementInterface
import com.pos.idm.endpoints.ResponseStatus
import com.pos.idm.security.jwt.JwtProvider
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