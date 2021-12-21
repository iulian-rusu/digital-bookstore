package com.pos.orders.business.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class BookOrder(
    @Id
    private var orderId: String?,
    private var date: String = "",
    private var items: List<BookOrderEntry> = listOf(),
    private var status: String = ""
) {
    fun getOrderId() = orderId
    fun setOrderId(value: String) {
        orderId = value
    }

    fun getDate() = date
    fun setDate(value: String) {
        date = value
    }

    fun getItems() = items
    fun setItems(value: List<BookOrderEntry>) {
        items = value
    }

    fun getStatus() = status
    fun setStatus(value: String) {
        status = value
    }
}