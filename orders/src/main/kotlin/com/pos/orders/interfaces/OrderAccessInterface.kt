package com.pos.orders.interfaces

import com.pos.orders.models.BookOrder
import com.pos.orders.models.requests.BookOrderRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface OrderAccessInterface {
    fun getAllOrders(clientId: Long, token: String?): ResponseEntity<CollectionModel<EntityModel<BookOrder>>>
    fun getOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<EntityModel<BookOrder>>
    fun postOrder(clientId: Long, request: BookOrderRequest, token: String?): ResponseEntity<EntityModel<BookOrder>>
    fun deleteAllOrders(clientId: Long, token: String?): ResponseEntity<Unit>
    fun deleteOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<Unit>
}