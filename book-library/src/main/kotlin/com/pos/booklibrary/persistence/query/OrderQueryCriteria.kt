package com.pos.booklibrary.persistence.query

import com.pos.booklibrary.models.BookOrder

class OrderQueryCriteria(orders: List<BookOrder>) : BasicQueryCriteria {
    private val paramMap: Map<String, UInt>

    init {
        paramMap = orders.associate { it.isbn to it.quantity }
    }

    override fun getQuery(): String {
        val queryBuilder = StringBuilder("UPDATE books INNER JOIN(")
        var index = 1
        paramMap.forEach { (isbn, quantity) ->
            queryBuilder.append("SELECT '$isbn' isbn, $quantity quantity ")
            if (index++ < paramMap.size)
                queryBuilder.append("UNION ")
        }
        queryBuilder.append(") B USING (isbn) SET books.stock = books.stock - B.quantity")
        return queryBuilder.toString()
    }

    override fun getParams() = paramMap
}