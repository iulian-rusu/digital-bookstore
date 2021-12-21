package com.pos.orders.business.models

object OrderStatus {
    val VALUES = listOf(
        "pending",
        "cancelled",
        "finished",
    )
    val PENDING = VALUES[0]
    val CANCELLED = VALUES[1]
    val FINISHED = VALUES[2]
}