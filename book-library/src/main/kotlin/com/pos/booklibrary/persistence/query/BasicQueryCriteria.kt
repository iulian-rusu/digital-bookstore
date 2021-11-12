package com.pos.booklibrary.persistence.query

interface BasicQueryCriteria {
    fun getQuery(): String
    fun getParams(): Map<String, Any>
}