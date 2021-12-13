package com.pos.identity.security.exceptions

import org.springframework.security.core.AuthenticationException

class PasswordAuthenticationException : AuthenticationException("Bad password")