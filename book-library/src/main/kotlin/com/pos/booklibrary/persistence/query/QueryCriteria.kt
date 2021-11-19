package com.pos.booklibrary.persistence.query

interface QueryCriteria {
    fun getQuery(): String
    fun getParams(): Map<String, Any>
}