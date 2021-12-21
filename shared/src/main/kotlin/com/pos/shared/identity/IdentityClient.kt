package com.pos.shared.identity

import com.pos.shared.*
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

class IdentityClient: WebServiceGatewaySupport() {
    fun authenticate(username: String, password: String): UserAuthenticationResponse {
        val request = UserAuthenticationRequest()
        request.username = username
        request.password = password
        return post(request)
    }

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

    private inline fun <reified R> post(request: Any) = webServiceTemplate.marshalSendAndReceive(request) as R
}