package com.pos.identity.security.exceptions

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(msg: String) : AuthenticationException(msg)