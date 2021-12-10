package com.pos.identity.persistence.query

abstract class ParametrizedQuery {
    val params: HashMap<String, Any?> = hashMapOf()

    abstract fun getSql(): String
}