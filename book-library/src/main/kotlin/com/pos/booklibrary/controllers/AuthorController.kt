package com.pos.booklibrary.controllers

import com.pos.booklibrary.persistence.query.SearchAuthorQuery
import com.pos.booklibrary.models.Author
import com.pos.booklibrary.services.AuthorAccessService
import com.pos.shared.security.jwt.JWT
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/book-library")
class AuthorController {
    @Autowired
    private lateinit var authorAccessService: AuthorAccessService

    @Operation(summary = "Get all authors")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Authors retrieved"),
            ApiResponse(responseCode = "400", description = "Bad request parameters")
        ]
    )
    @GetMapping("/authors")
    fun getAllAuthors(@RequestParam params: Map<String, String>) =
        authorAccessService.getAllAuthors(SearchAuthorQuery(params))

    @Operation(summary = "Get author specified by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Author retrieved"),
            ApiResponse(responseCode = "404", description = "Author not found")
        ]
    )
    @GetMapping("/authors/{id}")
    fun getAuthor(@PathVariable id: Long) = authorAccessService.getAuthor(id)

    @Operation(summary = "Create a single author")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Author created"),
            ApiResponse(responseCode = "406", description = "Missing or invalid author data")
        ]
    )
    @PostMapping("/authors")
    fun postAuthor(@RequestBody newAuthor: Author, @RequestHeader("Authorization") authHeader: String?) =
        authorAccessService.postAuthor(newAuthor, JWT.getToken(authHeader))

    @Operation(summary = "Update or create a single author")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Author updated"),
            ApiResponse(responseCode = "201", description = "Author created"),
            ApiResponse(responseCode = "406", description = "Missing required author data"),
            ApiResponse(responseCode = "409", description = "Error when inserting author data"),
        ]
    )
    @PutMapping("/authors/{id}")
    fun putAuthor(
        @PathVariable id: Long,
        @RequestBody newAuthor: Author,
        @RequestHeader("Authorization") authHeader: String?
    ) = authorAccessService.putAuthor(id, newAuthor, JWT.getToken(authHeader))

    @Operation(summary = "Delete a single author")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Author deleted"),
            ApiResponse(responseCode = "404", description = "Author not found")
        ]
    )
    @DeleteMapping("/authors/{id}")
    fun deleteAuthor(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String?) =
        authorAccessService.deleteAuthor(id, JWT.getToken(authHeader))
}