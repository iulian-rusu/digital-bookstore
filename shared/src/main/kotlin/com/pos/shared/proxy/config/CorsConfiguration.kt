package com.pos.shared.proxy.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class CorsConfiguration : WebMvcConfigurer {
    companion object {
        private const val BOOK_LIBRARY_PATH = "/api/proxy"
        private val CLIENT_HOST = System.getenv("CLIENT_HOST") ?: "localhost"
        private val CLIENT_PORT = System.getenv("CLIENT_PORT") ?: "3000"
        private val CLIENT_ORIGIN = "http://$CLIENT_HOST:$CLIENT_PORT"
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.apply {
            register("/identity/*", CLIENT_ORIGIN, "POST")
        }
    }

    fun CorsRegistry.register(pathPattern: String, origin: String, vararg methods: String) =
        addMapping("$BOOK_LIBRARY_PATH$pathPattern")
            .allowedOriginPatterns(origin)
            .allowedMethods(*methods)
            .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
            .exposedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
}