package com.pos.orders.controllers

import com.pos.orders.models.Order
import com.pos.orders.services.OrderAccessService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController {
    @Autowired
    private lateinit var orderAccessService: OrderAccessService

    @Operation(summary = "Get all orders for a client")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Orders retrieved")
    ])
    @GetMapping("/{clientId}")
    fun getAllOrders(@PathVariable(required = true) clientId: Long) = orderAccessService.getAllOrders(clientId)

    @Operation(summary = "Get a specific order")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Order retrieved"),
        ApiResponse(responseCode = "404", description = "Order not found")
    ])
    @GetMapping("/{clientId}/{orderId}")
    fun getOrder(@PathVariable(required = true) clientId: Long, @PathVariable(required = true) orderId: String) =
        orderAccessService.getOrder(clientId, orderId)

    @Operation(summary = "Create an order for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Order created"),
            ApiResponse(responseCode = "406", description = "Error creating order")
        ]
    )
    @PostMapping("/{clientId}")
    fun postOrder(@PathVariable(required = true) clientId: Long, @RequestBody order: Order) =
        orderAccessService.postOrder(clientId, order)

    @Operation(summary = "Create or update an order for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Order created/updated"),
            ApiResponse(responseCode = "406", description = "Error creating/updating order")
        ]
    )
    @PutMapping("/{clientId}/{orderId}")
    fun putOrder(
        @PathVariable(required = true) clientId: Long,
        @PathVariable(required = true) orderId: String,
        @RequestBody order: Order
    ) = orderAccessService.putOrder(clientId, orderId, order)

    @Operation(summary = "Delete all orders for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Orders deleted"),
            ApiResponse(responseCode = "404", description = "Client not found")
        ]
    )
    @DeleteMapping("/{clientId}")
    fun deleteAllOrders(@PathVariable(required = true) clientId: Long) = orderAccessService.deleteAllOrders(clientId)

    @Operation(summary = "Delete a specific order for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Order deleted"),
            ApiResponse(responseCode = "404", description = "Client/order not found")
        ]
    )
    @DeleteMapping("/{clientId}/{orderId}")
    fun deleteOrder(@PathVariable(required = true) clientId: Long, @PathVariable(required = true) orderId: String) =
        orderAccessService.deleteOrder(clientId, orderId)
}