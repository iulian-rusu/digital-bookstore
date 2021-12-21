package com.pos.identity.api.endpoints

import com.pos.identity.*
import com.pos.identity.business.interfaces.JwtManagementInterface
import com.pos.identity.business.interfaces.UserManagementInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ws.server.endpoint.annotation.Endpoint
import org.springframework.ws.server.endpoint.annotation.PayloadRoot
import org.springframework.ws.server.endpoint.annotation.RequestPayload
import org.springframework.ws.server.endpoint.annotation.ResponsePayload

@Endpoint
class IdentityManagementEndpoint {
    companion object {
        const val NAMESPACE_URL = "http://identity.pos.com"
    }

    @Autowired
    private lateinit var jwtManagementService: JwtManagementInterface

    @Autowired
    private lateinit var userManagementService: UserManagementInterface

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "userAuthenticationRequest")
    @ResponsePayload
    fun authenticate(@RequestPayload request: UserAuthenticationRequest) = userManagementService.authenticate(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "userRegistrationRequest")
    @ResponsePayload
    fun register(@RequestPayload request: UserRegistrationRequest) = userManagementService.register(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "userUpdateRequest")
    @ResponsePayload
    fun update(@RequestPayload request: UserUpdateRequest) = userManagementService.update(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "userDeletionRequest")
    @ResponsePayload
    fun delete(@RequestPayload request: UserDeletionRequest) = userManagementService.delete(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "tokenValidationRequest")
    @ResponsePayload
    fun validate(@RequestPayload request: TokenValidationRequest) = jwtManagementService.validate(request)

    @PayloadRoot(namespace = NAMESPACE_URL, localPart = "tokenDestructionRequest")
    @ResponsePayload
    fun destroy(@RequestPayload request: TokenDestructionRequest) = jwtManagementService.destroy(request)
}