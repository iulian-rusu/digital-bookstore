package com.pos.identity.services

import com.pos.identity.endpoints.ResponseStatus
import org.slf4j.LoggerFactory

abstract class GuardedScopeService(guardedClass: Class<*>) {
    protected val logger = LoggerFactory.getLogger(guardedClass)

    protected fun <Response> guardedScope(
        action: () -> Response,
        onError: (Exception) -> Response
    ): Response {
        return try {
            action()
        } catch (e: Exception) {
            logger.error("Scope error: ${e.message}")
            onError(e)
        }
    }

    protected fun errorStatus(e: Exception) = "${ResponseStatus.ERROR}: ${e.javaClass}"
    protected fun errorStatus(msg: String) = "${ResponseStatus.ERROR}: $msg"
}