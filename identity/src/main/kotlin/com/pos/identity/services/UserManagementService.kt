package com.pos.identity.services

import com.pos.identity.*
import com.pos.identity.interfaces.UserManagementInterface
import com.pos.identity.persistence.GenericQueryRepository
import com.pos.identity.persistence.UserRepository
import com.pos.identity.persistence.query.UpdateUserQuery
import com.pos.identity.security.Encoder
import com.pos.identity.security.exceptions.PasswordAuthenticationException
import com.pos.identity.security.UserRole
import com.pos.identity.security.exceptions.AuthorizationException
import com.pos.identity.security.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.management.relation.InvalidRoleValueException

@Service
class UserManagementService : GuardedSoapScope(UserManagementService::class.java), UserManagementInterface {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var genericQeryRepository: GenericQueryRepository

    override fun authenticate(request: UserAuthenticationRequest): UserAuthenticationResponse {
        val response = UserAuthenticationResponse()
        guardedScope {
            val databaseHash = userRepository.findPasswordHash(request.username)
            if (Encoder.matches(request.password, databaseHash)) {
                val savedUser = userRepository.findOne(request.username)
                response.token = jwtProvider.createToken(savedUser)
            } else {
                logger.warn("Invalid password attempt for username=${request.username}")
                throw PasswordAuthenticationException()
            }
        }
        return response
    }

    override fun register(request: UserRegistrationRequest): UserRegistrationResponse {
        val response = UserRegistrationResponse()
        guardedScope {
            val savedUser = userRepository.save(request)
            response.token = jwtProvider.createToken(savedUser)
        }
        return response
    }

    override fun update(request: UserUpdateRequest): UserUpdateResponse {
        val response = UserUpdateResponse()
        guardedScope {
            val details = jwtProvider.getUserDetails(request.token)

            if (request.password != null && request.userId != details.userId)
                throw AuthorizationException("update user password")

            request.role?.let {
                if (details.role != UserRole.ADMIN)
                    throw AuthorizationException("update user role")
                if (it !in UserRole.ROLES)
                    throw InvalidRoleValueException("Not a valid user role")
            }

            val query = UpdateUserQuery(
                userId = request.userId,
                passwordHash = request.password?.let { Encoder.hashString(it) },
                role = request.role
            )
            genericQeryRepository.execute(query)
            response.token = jwtProvider.createToken(userRepository.findOne(request.userId))
        }
        return response
    }

    override fun delete(request: UserDeletionRequest): UserDeletionResponse {
        val response = UserDeletionResponse()
        guardedScope {
            val details = jwtProvider.getUserDetails(request.token)

            if (details.userId != request.userId)
                throw AuthorizationException("delete user")

            val toDelete = userRepository.findOne(request.userId)
            userRepository.deleteOne(request)
            response.apply {
                userId = toDelete.userId
                username = toDelete.username
                role = toDelete.role
            }
        }
        return response
    }
}