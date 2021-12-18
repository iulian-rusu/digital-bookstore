package com.pos.orders.controllers

import com.pos.orders.models.Order
import com.pos.orders.models.PostOrderRequest
import com.pos.orders.services.OrderAccessService
import com.pos.shared.security.jwt.JWT
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
    @GetMapping("/client/{clientId}")
    fun getAllOrders(
        @PathVariable clientId: Long,
        @RequestHeader("Authorization") authHeader: String?
    ) = orderAccessService.getAllOrders(clientId, JWT.getToken(authHeader))

    @Operation(summary = "Get a specific order")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Order retrieved"),
        ApiResponse(responseCode = "404", description = "Order not found")
    ])
    @GetMapping("/client/{clientId}/order/{orderId}")
    fun getOrder(
        @PathVariable clientId: Long,
        @PathVariable orderId: String,
        @RequestHeader("Authorization") authHeader: String?
    ) = orderAccessService.getOrder(clientId, orderId, JWT.getToken(authHeader))

    @Operation(summary = "Create an order for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Order created"),
            ApiResponse(responseCode = "409", description = "Missing or invalid order data"),
            ApiResponse(responseCode = "406", description = "Error creating order")
        ]
    )
    @PostMapping("/client/{clientId}")
    fun postOrder(
        @PathVariable clientId: Long,
        @RequestBody request: PostOrderRequest,
        @RequestHeader("Authorization") authHeader: String?
    ) = orderAccessService.postOrder(clientId, request, JWT.getToken(authHeader))

    @Operation(summary = "Create or update an order for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Order created/updated"),
            ApiResponse(responseCode = "404", description = "Cannot find ordered item"),
            ApiResponse(responseCode = "406", description = "Missing or incorrect order data"),
            ApiResponse(responseCode = "409", description = "Cannot validate order data")
        ]
    )
    @PutMapping("/client/{clientId}/order/{orderId}")
    fun putOrder(
        @PathVariable clientId: Long,
        @PathVariable orderId: String,
        @RequestBody request: PostOrderRequest,
        @RequestHeader("Authorization") authHeader: String?
    ) = orderAccessService.putOrder(clientId, orderId, request, JWT.getToken(authHeader))

    @Operation(summary = "Delete all orders for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Orders deleted"),
            ApiResponse(responseCode = "404", description = "Client not found")
        ]
    )
    @DeleteMapping("/client/{clientId}")
    fun deleteAllOrders(@PathVariable clientId: Long, @RequestHeader("Authorization") authHeader: String?) =
        orderAccessService.deleteAllOrders(clientId, JWT.getToken(authHeader))

    @Operation(summary = "Delete a specific order for a client")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Order deleted"),
            ApiResponse(responseCode = "404", description = "Client/order not found")
        ]
    )
    @DeleteMapping("/client/{clientId}/order/{orderId}")
    fun deleteOrder(
        @PathVariable clientId: Long,
        @PathVariable orderId: String,
        @RequestHeader("Authorization") authHeader: String?
    ) = orderAccessService.deleteOrder(clientId, orderId, JWT.getToken(authHeader))
}