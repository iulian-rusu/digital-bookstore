package com.pos.orders.interfaces

import com.pos.orders.models.PostOrderRequest
import org.springframework.http.HttpStatus

interface OrderValidationInterface {
    fun validateOrder(request: PostOrderRequest): HttpStatus
}