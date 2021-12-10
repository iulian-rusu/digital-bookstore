package com.pos.idm.endpoints

import com.pos.idm.*
import com.pos.idm.interfaces.JwtManagementInterface
import com.pos.idm.interfaces.UserManagementInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.ws.server.endpoint.annotation.Endpoint
import org.springframework.ws.server.endpoint.annotation.PayloadRoot
import org.springframework.ws.server.endpoint.annotation.RequestPayload
import org.springframework.ws.server.endpoint.annotation.ResponsePayload

@Endpoint
class IdentityManagementEndpoint {
    companion object {
        const val NAMESPACE_URL = "http://pos.com/idm"
    }

    @Autowired
    private lateinit var jwtManagementService: JwtManagementInterface

    @Autowired
    private lateinit var userManagementService: UserManagementInterface

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "authRequest")
    @ResponsePayload
    fun authenticate(@RequestPayload request: AuthRequest) = userManagementService.authenticate(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "registerRequest")
    @ResponsePayload
    fun register(@RequestPayload request: RegisterRequest) = userManagementService.register(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "updateRequest")
    @ResponsePayload
    fun update(
        @RequestPayload request: UpdateRequest,
        @RequestHeader("Authorization") authHeader: String?
    ) = userManagementService.update(request, authHeader ?: "")

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "tokenValidationRequest")
    fun validate(@RequestPayload request: TokenValidationRequest) = jwtManagementService.validate(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "tokenDestructionRequest")
    fun destroy(@RequestPayload request: TokenDestructionRequest) = jwtManagementService.destroy(request)
}