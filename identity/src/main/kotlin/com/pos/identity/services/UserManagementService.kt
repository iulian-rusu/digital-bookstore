package com.pos.identity.services

import com.pos.identity.*
import com.pos.identity.interfaces.UserManagementInterface
import com.pos.identity.endpoints.ResponseStatus
import com.pos.identity.persistence.GenericQueryRepository
import com.pos.identity.persistence.UserRepository
import com.pos.identity.persistence.query.UpdateUserQuery
import com.pos.identity.security.Encoder
import com.pos.identity.security.UserRole
import com.pos.identity.security.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.management.relation.InvalidRoleValueException

@Service
class UserManagementService : GuardedScopeService(), UserManagementInterface {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var genericQeryRepository: GenericQueryRepository

    override fun authenticate(request: UserAuthenticationRequest): UserAuthenticationResponse {
        val response = UserAuthenticationResponse()
        return guardedScope(
            action = {
                val databaseHash = userRepository.findPasswordHash(request.username)
                if (Encoder.matches(request.password, databaseHash)) {
                    val savedUser = userRepository.findOne(request.username)
                    response.status = ResponseStatus.SUCCESS
                    response.token = jwtProvider.createToken(savedUser)
                } else {
                    logger.warn("authenticate(username=${request.username})")
                    response.status = errorStatus("Bad password")
                }
                response
            },
            onError = {
                response.apply {
                    status = errorStatus(it)
                }
            }
        )
    }

    override fun register(request: UserRegistrationRequest): UserRegistrationResponse {
        val response = UserRegistrationResponse()
        return guardedScope(
            action = {
                val savedUser = userRepository.save(request)
                response.apply {
                    status = ResponseStatus.SUCCESS
                    token = jwtProvider.createToken(savedUser)
                }
            },
            onError = {
                response.apply {
                    status = errorStatus(it)
                }
            }
        )
    }

    override fun update(request: UserUpdateRequest, authHeader: String): UserUpdateResponse {
        val response = UserUpdateResponse()
        return guardedScope(
            action = {
                val details = jwtProvider.getToken(authHeader)?.let { jwtProvider.getUserDetails(it) }

                if (details == null) {
                    response.status = ResponseStatus.UNAUTHORIZED
                    return@guardedScope response
                }

                if (request.password != null && request.userId != details.userId) {
                    response.status = ResponseStatus.FORBIDDEN
                    return@guardedScope response
                }

                request.role?.let {
                    if (details.role != UserRole.ADMIN) {
                        response.status = ResponseStatus.FORBIDDEN
                        return@guardedScope response
                    }
                    if (it !in UserRole.ROLES)
                        throw InvalidRoleValueException("Not a valid user role")
                }

                val query = UpdateUserQuery(
                    userId = request.userId,
                    passwordHash = request.password,
                    role = request.role
                )
                genericQeryRepository.execute(query)
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

    override fun delete(request: UserDeletionRequest, authHeader: String): UserDeletionResponse {
        val response = UserDeletionResponse()
        return guardedScope(
            action = {
                val details = jwtProvider.getToken(authHeader)?.let { jwtProvider.getUserDetails(it) }

                if (details == null) {
                    response.status = ResponseStatus.UNAUTHORIZED
                    return@guardedScope response
                }

                if (details.userId != request.userId) {
                    response.status = ResponseStatus.FORBIDDEN
                    return@guardedScope response
                }

                userRepository.deleteOne(request)
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