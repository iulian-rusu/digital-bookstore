package com.pos.orders.interfaces

import com.pos.orders.models.Order
import com.pos.orders.models.PostOrderRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface OrderAccessInterface {
    fun getAllOrders(clientId: Long, token: String?): ResponseEntity<CollectionModel<EntityModel<Order>>>
    fun getOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<EntityModel<Order>>
    fun postOrder(clientId: Long, request: PostOrderRequest, token: String?): ResponseEntity<EntityModel<Order>>
    fun putOrder(
        clientId: Long,
        orderId: String,
        request: PostOrderRequest,
        token: String?
    ): ResponseEntity<EntityModel<Order>>

    fun deleteAllOrders(clientId: Long, token: String?): ResponseEntity<Unit>
    fun deleteOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<Unit>
}