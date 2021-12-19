package com.pos.booklibrary.persistence.query

import com.pos.booklibrary.models.BookOrderRequest

class UpdateStockQuery(items: List<BookOrderRequest>) : ParametrizedQuery() {
    init {
        params += items.associate { it.isbn to it.quantity }
    }

    override fun getSql(): String {
        val sqlBuilder = StringBuilder("UPDATE books INNER JOIN(")
        var index = 1
        params.forEach { (isbn, quantity) ->
            sqlBuilder.append("SELECT '$isbn' isbn, $quantity quantity ")
            if (index++ < params.size)
                sqlBuilder.append("UNION ")
        }
        sqlBuilder.append(") B USING (isbn) SET books.stock = books.stock - B.quantity")
        return sqlBuilder.toString()
    }
}