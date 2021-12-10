package com.pos.identity.security.jwt

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(msg: String) : AuthenticationException(msg)