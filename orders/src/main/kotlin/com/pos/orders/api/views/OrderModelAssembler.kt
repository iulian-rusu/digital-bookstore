package com.pos.orders.api.views

import com.pos.orders.api.controllers.OrderController
import com.pos.orders.api.requests.BookOrderRequest
import com.pos.orders.api.requests.UpdateOrderRequest
import com.pos.orders.business.models.BookOrder
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class OrderModelAssembler : RepresentationModelAssembler<BookOrder, EntityModel<BookOrder>> {
    override fun toModel(order: BookOrder) = EntityModel.of(
        order,
        linkTo(methodOn(OrderController::class.java).getAllOrders(1, null)).withSelfRel()
    )

    fun toModelWithClientId(order: BookOrder, clientId: Long) = EntityModel.of(
        order,
        linkTo(methodOn(OrderController::class.java).getOrder(clientId, order.getOrderId() ?: "", null)).withSelfRel(),
        linkTo(methodOn(OrderController::class.java).postOrder(clientId, BookOrderRequest(listOf()), null))
            .withRel("create order"),
        linkTo(methodOn(OrderController::class.java).getAllOrders(clientId, null)).withRel("get client orders"),
        linkTo(
            methodOn(OrderController::class.java)
                .putOrder(clientId, order.getOrderId() ?: "", UpdateOrderRequest(""), null)
        ).withRel("update order"),
        linkTo(methodOn(OrderController::class.java).deleteOrder(clientId, order.getOrderId() ?: "", null))
            .withRel("delete order"),
    )
}