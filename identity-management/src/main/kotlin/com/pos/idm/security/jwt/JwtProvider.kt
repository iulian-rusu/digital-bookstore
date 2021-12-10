package com.pos.idm.security.jwt

import com.pos.idm.endpoints.IdentityManagementEndpoint
import com.pos.idm.models.User
import com.pos.idm.persistence.UserRepository
import com.pos.idm.security.SimpleUserDetails
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtProvider {
    companion object {
        val blacklist: MutableList<String> = mutableListOf()
    }

    @Autowired
    lateinit var jwtProperties: JwtProperties

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    fun createToken(user: User): String {
        val claims = Jwts.claims()
        claims.issuer = IdentityManagementEndpoint.NAMESPACE_URL
        claims.subject = user.userId.toString()
        claims.expiration = Date(Date().time + jwtProperties.durationMs)
        claims["jti"] = UUID.randomUUID()
        claims["role"] = user.role

        return Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secretKey)
            .compact()
    }

    fun getUserDetails(token: String): SimpleUserDetails {
        val user = userRepository.findOne(getUserId(token))
        return userDetailsService.loadUserByUsername(user.username) as SimpleUserDetails
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails = getUserDetails(token)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUserId(token: String) =
        Jwts.parser().setSigningKey(jwtProperties.secretKey).parseClaimsJws(token).body["sub"].toString().toLong()

    fun getToken(authHeader: String?): String? {
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.split(" ")[1]
        } else null
    }

    fun getToken(req: HttpServletRequest): String? {
        val authHeader = req.getHeader("Authorization")
        return getToken(authHeader)
    }

    fun validateToken(token: String): Boolean {
        if (blacklist.contains(token))
            return false
        return try {
            val claims = Jwts.parser().setSigningKey(jwtProperties.secretKey).parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            blacklist.add(token)
            throw JwtAuthenticationException(e.message ?: "JWT validation exception")
        }
    }

    fun destroyToken(token: String) {
        blacklist.add(token)
    }
}