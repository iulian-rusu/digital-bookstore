package com.pos.identity.security


import com.pos.identity.persistence.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class SimpleUserDetailsService: UserDetailsService {
    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = username?.let { userRepository.findOne(it) } ?: throw Exception("Username missing")
        return SimpleUserDetails(
            userId = user.userId,
            role = user.role,
            username = user.username
        )
    }
}