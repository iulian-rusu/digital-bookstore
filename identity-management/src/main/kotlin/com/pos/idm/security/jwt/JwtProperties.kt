package com.pos.idm.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    val secretKey: String = Base64.getEncoder().encodeToString("RestInPeace".toByteArray())
    val durationMs = 24 * 3600 * 1000
}