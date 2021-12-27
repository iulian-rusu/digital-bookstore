package com.pos.identity.business.services

import com.pos.identity.*
import com.pos.identity.business.security.Encoder
import com.pos.identity.business.security.exceptions.AuthorizationException
import com.pos.identity.business.security.exceptions.PasswordAuthenticationException
import com.pos.identity.business.security.jwt.JwtProvider
import com.pos.identity.business.services.interfaces.UserManagementInterface
import com.pos.identity.persistence.GenericQueryRepository
import com.pos.identity.persistence.UserRepository
import com.pos.identity.persistence.query.UpdateUserQuery
import com.pos.shared.security.UserRole
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
                response.sub = savedUser.userId
                response.role = savedUser.role
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
            val details = jwtProvider.getUser(request.token)
            if (details.role != UserRole.ADMIN)
                throw AuthorizationException("Not authorized to create users")

            val savedUser = userRepository.save(request)
            response.token = jwtProvider.createToken(savedUser)
            response.sub = savedUser.userId
            response.role = savedUser.role
        }
        return response
    }

    override fun update(request: UserUpdateRequest): UserUpdateResponse {
        val response = UserUpdateResponse()
        guardedScope {
            val details = jwtProvider.getUser(request.token)

            if (request.password != null && request.userId != details.userId)
                throw AuthorizationException("Not authorized to modify user password")

            request.role?.let {
                if (details.role != UserRole.ADMIN)
                    throw AuthorizationException("Not authorized to modify user role")
                if (it !in UserRole.VALUES)
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
            val details = jwtProvider.getUser(request.token)

            if (details.role != UserRole.ADMIN && details.userId != request.userId)
                throw AuthorizationException("Not authorized to delete user")

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