package com.pos.booklibrary.models

import javax.persistence.*

@Entity
@Table(name = "authors")
class Author(
    @Id
    @GeneratedValue
    @Column(name = "author_id")
    private var id: Long = -1,

    @Column(name = "first_name")
    private var firstName: String = "",

    @Column(name = "last_name")
    private var lastName: String = ""
) {
    fun getId() = id

    fun setId(value: Long) {
        id = value
    }

    fun getFirstName() = firstName

    fun setFirstName(value: String) {
        firstName = value
    }

    fun getLastName() = lastName

    fun setLasttName(value: String) {
        lastName = value
    }
}
