package com.pos.shared.ws

import com.pos.shared.TokenDestructionRequest
import com.pos.shared.TokenDestructionResponse
import com.pos.shared.TokenValidationRequest
import com.pos.shared.TokenValidationResponse
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

class IdentityClient: WebServiceGatewaySupport() {
    fun validateToken(token: String): TokenValidationResponse {
        val request = TokenValidationRequest()
        request.token = token
        return post(request)
    }

    fun destroyToken(token: String): TokenDestructionResponse {
        val request =  TokenDestructionRequest()
        request.token = token
        return post(request)
    }

    inline fun <reified R> post(request: Any) = webServiceTemplate.marshalSendAndReceive(request) as R
}