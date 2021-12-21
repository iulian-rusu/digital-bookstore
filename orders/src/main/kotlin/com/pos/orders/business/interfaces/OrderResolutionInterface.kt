package com.pos.orders.business.interfaces

import com.pos.orders.business.models.BookOrder
import com.pos.orders.api.requests.BookOrderRequest

interface OrderResolutionInterface {
    fun resolveOrder(request: BookOrderRequest): BookOrder
}