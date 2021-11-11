package com.pos.orders.views

import com.pos.orders.controllers.OrderController
import com.pos.orders.models.Order
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class OrderModelAssembler: RepresentationModelAssembler<Order, EntityModel<Order>> {
    override fun toModel(order: Order) = EntityModel.of(
        order,
        linkTo(methodOn(OrderController::class.java).getAllOrders(1 )).withSelfRel()
    )

    fun toModelWithClientId(order: Order, clientId: Long) = EntityModel.of(
        order,
        linkTo(methodOn(OrderController::class.java).getOrder(clientId, order.getOrderId() ?: "")).withSelfRel(),
        linkTo(methodOn(OrderController::class.java).getAllOrders(clientId)).withRel("clientOrders")
    )
}