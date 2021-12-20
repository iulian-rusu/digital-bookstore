package com.pos.booklibrary.persistence.query

class SearchBookListQuery(private val isbns: List<String>) : ParametrizedQuery() {
    init {
        isbns.forEachIndexed { index, isbn ->
            params["isbn$index"] = isbn
        }
    }

    override fun getSql(): String {
        val sqlBuilder = StringBuilder("SELECT * FROM books WHERE isbn IN ")
        val isbnList = mutableListOf<String>()
        for (i in isbns.indices) {
            isbnList.add(":isbn$i")
        }
        sqlBuilder.append(isbnList.joinToString(separator = ", ", prefix = "(", postfix = ")"))
        return sqlBuilder.toString()
    }
}