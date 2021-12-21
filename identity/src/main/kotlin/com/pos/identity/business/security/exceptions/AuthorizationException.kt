package com.pos.identity.business.security.exceptions

class AuthorizationException(msg: String): RuntimeException("Not authorized for action '$msg'")