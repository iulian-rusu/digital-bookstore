package com.pos.orders.api.requests

data class BookOrderRequestEntry(
    var isbn: String,
    var quantity: Int
)
