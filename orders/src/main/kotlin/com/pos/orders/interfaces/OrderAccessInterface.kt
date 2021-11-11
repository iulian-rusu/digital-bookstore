package com.pos.orders.interfaces

import com.pos.orders.models.Order
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity

interface OrderAccessInterface {
    fun getAllOrders(clientId: Long): CollectionModel<EntityModel<Order>>
    fun getOrder(clientId: Long, orderId: String): ResponseEntity<EntityModel<Order>>
    fun postOrder(clientId: Long, order: Order): ResponseEntity<EntityModel<Order>>
    fun putOrder(clientId: Long, orderId: String, order: Order): ResponseEntity<EntityModel<Order>>
    fun deleteAllOrders(clientId: Long): ResponseEntity<Unit>
    fun deleteOrder(clientId: Long, orderId: String): ResponseEntity<Unit>
}