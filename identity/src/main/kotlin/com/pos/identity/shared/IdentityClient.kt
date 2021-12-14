package com.pos.identity.shared

import com.pos.identity.TokenValidationRequest
import com.pos.identity.TokenValidationResponse
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

class IdentityClient: WebServiceGatewaySupport() {
    fun validateToken(token: String): TokenValidationResponse {
        val request = TokenValidationRequest()
        request.token = token
        return webServiceTemplate.marshalSendAndReceive(request) as TokenValidationResponse
    }
}