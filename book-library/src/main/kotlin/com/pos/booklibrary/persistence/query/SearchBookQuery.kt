package com.pos.booklibrary.persistence.query

class SearchBookQuery(queryParams: Map<String, String>) : PagedSearchQuery(queryParams) {
    private val title: String
    private val publisher: String
    private val publishYear: Int
    private val genre: String

    init {
        title = queryParams["title"] ?: ""
        publisher = queryParams["publisher"] ?: ""
        publishYear = queryParams["publish_year"]?.toIntOrNull() ?: -1
        genre = queryParams["genre"] ?: ""

        params["title"] = title
        params["genre"] = genre
        params["publishYear"] = publishYear
        params["publisher"] = publisher
    }

    override fun getSql(): String {
        val sqlBuilder = StringBuilder("SELECT * FROM books")
        val conditionBuilder = StringBuilder(" WHERE")
        val initialLength = conditionBuilder.length
        if (exactMatch) {
            if (title.isNotEmpty())
                conditionBuilder.append(" title = :title AND")
            if (publisher.isNotEmpty())
                conditionBuilder.append(" publisher = :publisher AND")
            if (genre.isNotEmpty())
                conditionBuilder.append(" genre = :genre AND")
        } else {
            conditionBuilder.append(
                " LOCATE(LOWER(:title), LOWER(title)) AND" +
                        " LOCATE(LOWER(:publisher), LOWER(publisher)) AND" +
                        " LOCATE(LOWER(:genre), LOWER(genre)) AND"
            )
        }
        if (publishYear >= 0)
            conditionBuilder.append(" publish_year = :publishYear")
        else
            conditionBuilder.append(" 1")
        conditionBuilder.append(" LIMIT :itemsPerPage OFFSET :offset")
        if (conditionBuilder.length > initialLength)
            sqlBuilder.append(conditionBuilder)
        return sqlBuilder.toString()
    }
}