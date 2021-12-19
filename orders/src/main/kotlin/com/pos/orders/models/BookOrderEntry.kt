package com.pos.orders.models

data class BookOrderEntry(
   var isbn: String = "",
   var price: Int = 0,
   var quantity: Int = 0
)