package com.pos.orders.services

import com.pos.orders.interfaces.OrderValidationInterface
import com.pos.orders.models.PostOrderRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.collections.HashMap

@Service
class OrderValidationService : OrderValidationInterface {
    companion object {
        private val bookLibraryHost = System.getenv("BOOK_LIBRARY_HOST") ?: "localhost"
        private val bookLibraryPort = System.getenv("BOOK_LIBRARY_PORT") ?: "8080"
        private val bookEndpoint = "http://$bookLibraryHost:$bookLibraryPort/api/book-library/books"
        private val responseType = HashMap<String, Any?>()::class.java
    }

    private val logger = LoggerFactory.getLogger(OrderValidationService::class.java)

    override fun validateOrder(request: PostOrderRequest): HttpStatus {
        if (!isValidRequest(request))
            return HttpStatus.NOT_ACCEPTABLE

        val restTemplate = RestTemplate()
        try {
            request.items.forEach {
                val url = "$bookEndpoint/${it.isbn}"
                val response = restTemplate.getForObject(url, responseType)
                val stock = response?.get("stock") as Int?
                if (stock == null) {
                    logger.error("Cannot find stock for ISBN=${it.isbn}")
                    return HttpStatus.NOT_FOUND
                }
                if (it.quantity > stock) {
                    logger.error("Not enough stock for ISBN=${it.isbn}")
                    return HttpStatus.CONFLICT
                }
            }
            return HttpStatus.OK
        } catch (e: HttpClientErrorException.NotFound) {
            logger.error("Not Found: $e")
            return HttpStatus.NOT_FOUND
        } catch (e: Exception) {
            logger.error("validateOrder(): $e")
            return HttpStatus.CONFLICT
        }
    }

    private fun isValidRequest(request: PostOrderRequest) = request.items.all {
        it.price > 0 && it.quantity > 0 && it.isbn.isNotEmpty()
    }
}