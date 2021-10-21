package com.pos.booklibrary.persistence

import com.pos.booklibrary.models.Author
import org.springframework.data.repository.CrudRepository

interface AuthorRepository : CrudRepository<Author, Long>