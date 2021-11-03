package com.pos.booklibrary.models

interface BasicBook {
    fun getIsbn(): String
    fun setIsbn(value: String)
    fun getTitle(): String
    fun setTitle(value: String)
}