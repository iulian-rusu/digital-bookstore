package com.pos.orders.interfaces

import com.pos.orders.models.BookOrder
import com.pos.orders.models.requests.BookOrderRequest

interface OrderResolutionInterface {
    fun resolveOrder(request: BookOrderRequest): BookOrder
}