package com.pos.booklibrary.persistence.query

abstract class BasicQueryCriteria(queryParams: Map<String, String>) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
        const val DEFAULT_EXACT_MATCH = false
    }

    private val page: Int
    protected val itemsPerPage: Int
    protected val exactMatch: Boolean
    protected val offset: Int

    init {
        page = queryParams["page"]?.toIntOrNull() ?: DEFAULT_PAGE
        itemsPerPage = queryParams["items_per_page"]?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        exactMatch = queryParams["match"]?.let { it == "exact" } ?: DEFAULT_EXACT_MATCH
        offset = (page - 1) * itemsPerPage
    }

    abstract fun getQuery(): String
    abstract fun getParams(): Map<String, Any>
}