package com.pos.booklibrary.config

import com.pos.identity.shared.IdentityClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.oxm.jaxb.Jaxb2Marshaller

@Configuration
class IdentityClientConfig {
    // TODO remove redundant configs?
    companion object {
        val IDENTITY_HOST = System.getenv("IDENTITY_HOST") ?: "localhost"
        val IDENTITY_PORT = System.getenv("IDENTITY_PORT") ?: "8082"
    }

    @Bean
    fun marshaller() = Jaxb2Marshaller().apply {
        contextPath = "com.pos.identity"
    }

    @Bean
    fun identityClient(jaxb2Marshaller: Jaxb2Marshaller) = IdentityClient().apply {
        defaultUri = "http://$IDENTITY_HOST:$IDENTITY_PORT/api"
        marshaller = jaxb2Marshaller
        unmarshaller = jaxb2Marshaller
    }
}