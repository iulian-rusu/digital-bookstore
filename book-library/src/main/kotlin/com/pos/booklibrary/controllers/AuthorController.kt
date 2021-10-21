package com.pos.booklibrary.controllers

import com.pos.booklibrary.models.Author
import com.pos.booklibrary.services.AuthorAccessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/book-library")
class AuthorController {
    @Autowired
    private lateinit var authorAccessService: AuthorAccessService

    @GetMapping("/authors")
    fun getAllAuthors() = authorAccessService.getAllAuthors()

    @GetMapping("/authors/{id}")
    fun getAuthor(@PathVariable id: Long) = authorAccessService.getAuthor(id)

    @PostMapping("/authors")
    fun postAuthor(@RequestBody newAuthor: Author) = authorAccessService.postAuthor(newAuthor)

    @PutMapping("/authors/{id}")
    fun putAuthor(@PathVariable id: Long, @RequestBody newAuthor: Author) = authorAccessService.putAuthor(id, newAuthor)

    @DeleteMapping("/authors/{id}")
    fun deleteAuthor(@PathVariable id: Long) = authorAccessService.deleteAuthor(id)
}