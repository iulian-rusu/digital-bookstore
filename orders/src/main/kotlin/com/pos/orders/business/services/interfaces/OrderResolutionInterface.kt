package com.pos.orders.business.services.interfaces

import com.pos.orders.api.requests.BookOrderRequest
import com.pos.orders.business.models.BookOrder

interface OrderResolutionInterface {
    fun resolveOrder(request: BookOrderRequest): BookOrder
}