package com.pos.identity.business.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.xml.soap.SOAPException

abstract class GuardedSoapScope(guardedClass: Class<*>) {
    protected val logger: Logger = LoggerFactory.getLogger(guardedClass)

    protected fun guardedScope(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            logger.error("Scope error: ${e.message}")
            errorHandler(e)
        }
    }

    private fun errorHandler(e: Exception): Nothing = throw SOAPException(e.message, e)
}