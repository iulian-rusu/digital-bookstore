package com.pos.orders.services

import com.pos.orders.interfaces.OrderResolutionInterface
import com.pos.orders.models.BookOrder
import com.pos.orders.models.BookOrderEntry
import com.pos.orders.models.OrderStatus
import com.pos.orders.models.requests.PostBookOrderRequest
import com.pos.shared.ws.IdentityAuthorized
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct

@Service
class OrderResolutionService : IdentityAuthorized(), OrderResolutionInterface {
    companion object {
        private val bookLibraryHost = System.getenv("BOOK_LIBRARY_HOST") ?: "localhost"
        private val bookLibraryPort = System.getenv("BOOK_LIBRARY_PORT") ?: "8080"
        private val managerUsername = System.getenv("MANAGER_USERNAME") ?: ""
        private val managerPassword = System.getenv("MANAGER_PASSWORD") ?: ""
        private val bookEndpoint = "http://$bookLibraryHost:$bookLibraryPort/api/book-library/books"
        private val responseType = HashMap<String, Any?>()::class.java
        private val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        private var managerToken: String = ""
        private var headers: HttpHeaders = HttpHeaders()
    }

    @PostConstruct
    fun resolveIdentity() {
        managerToken = getTokenFor(managerUsername, managerPassword)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        headers.set("Authorization", "Bearer $managerToken")
    }

    override fun resolveOrder(request: PostBookOrderRequest): BookOrder {
        val restTemplate = RestTemplate()
        val resolvedItems = mutableListOf<BookOrderEntry>()

        request.items.forEach {
            val url = "$bookEndpoint/${it.isbn}"
            val bookJson = restTemplate.getForObject(url, responseType)
            val stock = bookJson?.get("stock")!! as Int
            if (stock < it.quantity)
                throw RuntimeException("Not enough stock for book with ISBN=${it.isbn}")
            val price = bookJson["price"] as Int

            bookJson["stock"] = stock - it.quantity
            val entity = HttpEntity(bookJson, headers)
            restTemplate.put("$bookEndpoint/${it.isbn}", entity)
            resolvedItems.add(BookOrderEntry(it.isbn, price, it.quantity))
        }
        return BookOrder(null, dateFormat.format(Date()), resolvedItems, OrderStatus.PENDING)
    }
}