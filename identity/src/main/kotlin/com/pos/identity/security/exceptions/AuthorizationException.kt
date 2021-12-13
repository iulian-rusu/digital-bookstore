package com.pos.identity.security.exceptions

class AuthorizationException(msg: String): RuntimeException("Not authorized for action '$msg'")