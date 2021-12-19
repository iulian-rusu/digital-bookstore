package com.pos.orders.interfaces

import com.pos.orders.models.BookOrder
import com.pos.orders.models.requests.PostBookOrderRequest

interface OrderResolutionInterface {
    fun resolveOrder(request: PostBookOrderRequest): BookOrder
}