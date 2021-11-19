package com.pos.booklibrary.persistence.query

interface ParametrizedQuery {
    fun getSql(): String
    fun getParams(): Map<String, Any>
}