package com.pos.booklibrary.persistence.query

import com.pos.booklibrary.api.requests.StockUpdateRequest

class UpdateBookStockQuery(request: StockUpdateRequest) : ParametrizedQuery() {
    private val items = request.items

    init {
        items.forEachIndexed { index, item ->
            params["isbn$index"] = item.isbn
            params["quantity$index"] = item.quantity
        }
    }

    override fun getSql(): String {
        val sqlBuilder = StringBuilder("UPDATE books INNER JOIN(")
        val stockTable = mutableListOf<String>()
        items.forEachIndexed { index, _ ->
            stockTable.add("SELECT :isbn$index isbn, :quantity$index quantity ")
        }
        sqlBuilder.append(stockTable.joinToString(separator = "UNION "))
        sqlBuilder.append(") B USING (isbn) SET books.stock = books.stock - B.quantity;")
        return sqlBuilder.toString()
    }
}