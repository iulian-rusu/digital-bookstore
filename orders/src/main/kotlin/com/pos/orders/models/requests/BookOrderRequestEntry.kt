package com.pos.orders.models.requests

data class BookOrderRequestEntry(
    var isbn: String,
    var quantity: Int
)
