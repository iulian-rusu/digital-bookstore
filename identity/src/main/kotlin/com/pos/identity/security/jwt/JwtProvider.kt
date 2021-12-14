package com.pos.identity.security.jwt

import com.pos.identity.endpoints.IdentityManagementEndpoint
import com.pos.identity.models.User
import com.pos.identity.persistence.UserRepository
import com.pos.identity.security.exceptions.JwtAuthenticationException
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtProvider {
    companion object {
        private val blacklist: MutableList<String> = mutableListOf()

        fun getToken(authHeader: String?): String? {
            return if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader.split(" ")[1]
            } else null
        }

        fun getToken(req: HttpServletRequest): String? {
            val authHeader = req.getHeader("Authorization")
            return getToken(authHeader)
        }

        fun destroyToken(token: String) {
            blacklist.add(token)
        }
    }

    @Autowired
    lateinit var jwtProperties: JwtProperties

    @Autowired
    private lateinit var userRepository: UserRepository


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

    fun getUser(token: String) = userRepository.findOne(getUserId(token))

    fun getUserId(token: String) =
        Jwts.parser().setSigningKey(jwtProperties.secretKey).parseClaimsJws(token).body["sub"].toString().toLong()

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
}