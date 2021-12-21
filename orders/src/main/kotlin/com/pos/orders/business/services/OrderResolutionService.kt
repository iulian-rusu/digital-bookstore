package com.pos.orders.business.services

import com.pos.orders.api.requests.BookOrderRequest
import com.pos.orders.business.interfaces.OrderResolutionInterface
import com.pos.orders.business.models.BookOrder
import com.pos.orders.business.models.BookOrderEntry
import com.pos.orders.business.models.OrderStatus
import com.pos.shared.identity.IdentityAuthorized
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct

typealias ResponseType = LinkedHashMap<String, LinkedHashMap<String, List<LinkedHashMap<String, Any>>>>

@Service
class OrderResolutionService : IdentityAuthorized(), OrderResolutionInterface {
    companion object {
        private val bookLibraryHost = System.getenv("BOOK_LIBRARY_HOST") ?: "localhost"
        private val bookLibraryPort = System.getenv("BOOK_LIBRARY_PORT") ?: "8080"
        private val managerUsername = System.getenv("MANAGER_USERNAME") ?: ""
        private val managerPassword = System.getenv("MANAGER_PASSWORD") ?: ""
        private val bookEndpoint = "http://$bookLibraryHost:$bookLibraryPort/api/book-library/books/stocks"
        private val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        private var managerToken: String = ""
        private val headers: HttpHeaders = HttpHeaders()
        private val objectTypeClass = ResponseType()::class.java
    }

    @PostConstruct
    fun resolveIdentity() {
        managerToken = getTokenFor(managerUsername, managerPassword)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        headers.set("Authorization", "Bearer $managerToken")
    }

    override fun resolveOrder(request: BookOrderRequest): BookOrder {
        val restTemplate = RestTemplate()
        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForEntity(bookEndpoint, entity, objectTypeClass)
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

        return BookOrder(null, dateFormat.format(Date()), resolvedItems, OrderStatus.PENDING)
    }
}