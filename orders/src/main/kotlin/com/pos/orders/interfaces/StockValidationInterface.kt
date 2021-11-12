package com.pos.orders.interfaces

import com.pos.orders.models.Order

interface StockValidationInterface {
    fun postOrder(order: Order): Boolean
}