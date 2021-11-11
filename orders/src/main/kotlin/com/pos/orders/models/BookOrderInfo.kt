package com.pos.orders.models

class BookOrderInfo(
    private var isbn: String = "",
    private var price: Int = -1,
    private var quantity: Int = -1
) {
    fun getIsbn() = isbn
    fun setIsbn(value: String) {
        isbn = value
    }

    fun getPrice() = price
    fun setPrice(value: Int) {
        price = value
    }

    fun getQuantity() = quantity
    fun setQuantity(value: Int) {
        quantity = value
    }
}