package com.pos.orders.models

data class BookOrderInfo(
   var isbn: String = "",
   var price: Int = 0,
   var quantity: Int = 0
)