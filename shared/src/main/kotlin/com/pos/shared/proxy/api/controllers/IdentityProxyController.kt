package com.pos.shared.proxy.api.controllers

import com.pos.shared.UserUpdateRequest
import com.pos.shared.UserUpdateResponse
import com.pos.shared.identity.IdentityClient
import com.pos.shared.proxy.api.requests.RestUserAuthenticationRequest
import com.pos.shared.proxy.api.requests.RestUserUpdateRequest
import com.pos.shared.proxy.api.responses.RestUserAuthenticationResponse
import com.pos.shared.proxy.api.responses.RestUserUpdateResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/proxy/identity")
class IdentityProxyController {
    @Autowired
    private lateinit var identityClient: IdentityClient

    @PostMapping("/auth")
    fun postAuth(@RequestBody request: RestUserAuthenticationRequest): ResponseEntity<RestUserAuthenticationResponse> {
        return try {
            val response = identityClient.authenticate(request.username, request.password)
            ResponseEntity.ok(
                RestUserAuthenticationResponse(
                    token = response.token,
                    userId = response.sub,
                    role = response.role
                )
            )
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
        }
    }

    @PostMapping("/update")
    fun postUpdate(@RequestBody request: RestUserUpdateRequest): ResponseEntity<RestUserUpdateResponse> {
        return try {
            val soapRequest = UserUpdateRequest()
            soapRequest.apply {
                token = request.token
                userId = request.userId
                password = request.password
                role = request.role
            }
            val response = identityClient.post<UserUpdateResponse>(soapRequest)
            ResponseEntity.ok(
                RestUserUpdateResponse(
                    token = response.token
                )
            )
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
        }
    }
}