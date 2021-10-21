package com.pos.booklibrary.persistence

import com.pos.booklibrary.models.Book
import org.springframework.data.repository.CrudRepository

interface BookRepository : CrudRepository<Book, String>