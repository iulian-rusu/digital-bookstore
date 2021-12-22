package com.pos.orders.business.services

import com.fasterxml.jackson.databind.JsonNode
import com.pos.orders.api.requests.BookOrderRequest
import com.pos.orders.business.models.BookOrder
import com.pos.orders.business.models.BookOrderEntry
import com.pos.orders.business.models.OrderStatus
import com.pos.orders.business.services.interfaces.OrderResolutionInterface
import com.pos.shared.identity.IdentityAuthorized
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct

@Service
class OrderResolutionService : IdentityAuthorized(), OrderResolutionInterface {
    companion object {
        private val BOOK_LIBRARY_HOST = System.getenv("BOOK_LIBRARY_HOST") ?: "localhost"
        private val BOOK_LIBRARY_PORT = System.getenv("BOOK_LIBRARY_PORT") ?: "8080"
        private val MANAGER_USERNAME = System.getenv("MANAGER_USERNAME") ?: ""
        private val MANAGER_PASSWORD = System.getenv("MANAGER_PASSWORD") ?: ""
        private val BOOK_ENDPOINT = "http://$BOOK_LIBRARY_HOST:$BOOK_LIBRARY_PORT/api/book-library/books/stocks"
        private val DATE_FORMAT = SimpleDateFormat("dd-MMM-yyyy")
        private var MANAGER_TOKEN: String = ""
        private val REQUEST_HEADERS: HttpHeaders = HttpHeaders()
    }

    @PostConstruct
    fun resolveIdentity() {
        MANAGER_TOKEN = getTokenFor(MANAGER_USERNAME, MANAGER_PASSWORD)
        REQUEST_HEADERS.contentType = MediaType.APPLICATION_JSON
        REQUEST_HEADERS.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        REQUEST_HEADERS.set("Authorization", "Bearer $MANAGER_TOKEN")
    }

    override fun resolveOrder(request: BookOrderRequest): BookOrder {
        val entity = HttpEntity(request, REQUEST_HEADERS)
        val response = post(entity)
        if (response.statusCode != HttpStatus.OK)
            throw RuntimeException("Error validating order request")

        val resolvedItems = mutableListOf<BookOrderEntry>()
        val embedded = response.body
            ?.get("_embedded")
            ?: throw RuntimeException("Misssing '_embedded' field in response boddy")
        val bookList = embedded["bookList"] ?: throw RuntimeException("Misssing 'bookList' field in response boddy")
        bookList.forEachIndexed { index, book ->
            val isbn = request.items[index].isbn
            val price = book["price"].toString().toInt()
            val quantity = request.items[index].quantity
            resolvedItems.add(BookOrderEntry(isbn, price, quantity))
        }

        return BookOrder(null, DATE_FORMAT.format(Date()), resolvedItems, OrderStatus.PENDING)
    }

    private inline fun <reified T> post(entity: HttpEntity<T>): ResponseEntity<JsonNode> {
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(BOOK_ENDPOINT, entity, JsonNode::class.java)
        if (response.statusCode == HttpStatus.FORBIDDEN) {
            MANAGER_TOKEN = getTokenFor(MANAGER_USERNAME, MANAGER_PASSWORD)
            return restTemplate.postForEntity(BOOK_ENDPOINT, entity, JsonNode::class.java)
        }
        return response
    }
}