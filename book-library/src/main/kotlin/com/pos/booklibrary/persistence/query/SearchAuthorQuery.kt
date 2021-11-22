package com.pos.booklibrary.persistence.query

class SearchAuthorQuery(queryParams: Map<String, String>) : PagedSearchQuery(queryParams) {
    private val firstName: String
    private val lastName: String

    init {
        firstName = queryParams["first_name"] ?: ""
        lastName = queryParams["last_name"] ?: ""

        params["firstName"] = firstName
        params["lastName"] = lastName
    }

    override fun getSql(): String {
        val sqlBuilder = StringBuilder("SELECT * FROM authors")
        val conditionBuilder = StringBuilder(" WHERE")
        val initialLength = conditionBuilder.length
        if (exactMatch) {
            if (firstName.isNotEmpty())
                conditionBuilder.append(" first_name = :firstName AND")
            if (lastName.isNotEmpty())
                conditionBuilder.append(" last_name = :lastName AND")
        } else {
            conditionBuilder.append(
                " LOCATE(LOWER(:firstName), LOWER(first_name)) AND" +
                        " LOCATE(LOWER(:lastName), LOWER(last_name)) AND"
            )
        }
        conditionBuilder.append(" 1 LIMIT :itemsPerPage OFFSET :offset")
        if (conditionBuilder.length > initialLength)
            sqlBuilder.append(conditionBuilder)
        return sqlBuilder.toString()
    }
}
