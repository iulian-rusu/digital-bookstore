package com.pos.orders.api.views

import com.pos.orders.api.controllers.OrderController
import com.pos.orders.business.models.BookOrder
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class OrderModelAssembler: RepresentationModelAssembler<BookOrder, EntityModel<BookOrder>> {
    override fun toModel(order: BookOrder) = EntityModel.of(
        order,
        linkTo(methodOn(OrderController::class.java).getAllOrders(1 , null)).withSelfRel()
    )

    fun toModelWithClientId(order: BookOrder, clientId: Long) = EntityModel.of(
        order,
        linkTo(methodOn(OrderController::class.java).getOrder(clientId, order.getOrderId() ?: "", null)).withSelfRel(),
        linkTo(methodOn(OrderController::class.java).getAllOrders(clientId, null)).withRel("clientOrders")
    )
}