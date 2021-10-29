package com.pos.booklibrary.controllers.query

import com.pos.booklibrary.models.Author

data class AuthorQueryCriteria(
    var firstName: String = "",
    var lastName: String = "",
    var exactMatch: Boolean = false
) {
    constructor(queryParams: Map<String, String>) : this() {
        queryParams.apply {
            get("first_name")?.let { firstName = it }
            get("last_name")?.let { lastName = it }
            get("match")?.let { exactMatch = it == "exact" }
        }
    }

    fun check(author: Author) = if (exactMatch) checkExact(author) else checkPartial(author)

    private fun checkExact(author: Author): Boolean {
        if (firstName.isNotEmpty() && author.getFirstName() != firstName)
            return false
        if (lastName.isNotEmpty() && author.getLastName() != lastName)
            return false
        return true
    }

    private fun checkPartial(author: Author): Boolean {
        if (firstName.isNotEmpty() && !author.getFirstName().contains(firstName, ignoreCase = true))
            return false
        if (lastName.isNotEmpty() && !author.getLastName().contains(lastName, ignoreCase = true))
            return false
        return true
    }
}
