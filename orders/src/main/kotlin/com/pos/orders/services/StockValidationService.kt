package com.pos.orders.services

import com.pos.orders.interfaces.StockValidationInterface
import com.pos.orders.models.Order
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StockValidationService : StockValidationInterface {
    companion object {
        private val bookLibraryHost = System.getenv("BOOK_LIBRARY_HOST") ?: "localhost"
        private val bookLibraryPort = System.getenv("BOOK_LIBRARY_PORT") ?: "8080"
        private val stockEndpoint = "http://$bookLibraryHost:$bookLibraryPort/api/book-library/orders"
    }

    override fun postOrder(order: Order): Boolean {
        val restTemplate = RestTemplate()
        val requestBody = JSONArray()

        order.getItems().forEach {
            val obj = JSONObject(mapOf("isbn" to it.getIsbn(), "quantity" to it.getQuantity()))
            requestBody.add(obj)
        }
        return try {
            val response = restTemplate.postForEntity(stockEndpoint, requestBody, JSONArray::class.java)
            response.statusCode == HttpStatus.ACCEPTED
        } catch (e: Exception) {
            println(e)
            false
        }
    }
}