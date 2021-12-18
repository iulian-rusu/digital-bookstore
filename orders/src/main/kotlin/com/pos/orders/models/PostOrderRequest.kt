package com.pos.orders.models

data class PostOrderRequest(var items: List<BookOrderInfo> = listOf())
