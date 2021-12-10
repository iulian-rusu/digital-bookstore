package com.pos.idm.services

import com.pos.idm.endpoints.ResponseStatus
import org.slf4j.LoggerFactory

abstract class GuardedScopeService {
    protected val logger = LoggerFactory.getLogger(GuardedScopeService::class.java)

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