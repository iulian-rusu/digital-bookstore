package com.pos.orders.business.models

data class BookOrderEntry(
   var isbn: String = "",
   var title: String = "",
   var price: Int = 0,
   var quantity: Int = 0
)