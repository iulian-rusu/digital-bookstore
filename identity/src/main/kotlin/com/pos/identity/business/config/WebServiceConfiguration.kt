package com.pos.identity.business.config

import com.pos.identity.endpoints.IdentityManagementEndpoint
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.ws.config.annotation.EnableWs
import org.springframework.ws.config.annotation.WsConfigurerAdapter
import org.springframework.ws.transport.http.MessageDispatcherServlet
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition
import org.springframework.xml.xsd.SimpleXsdSchema
import org.springframework.xml.xsd.XsdSchema

@EnableWs
@Configuration
class WebServiceConfiguration : WsConfigurerAdapter() {
    @Bean
    fun messageDispatcherServlet(applicationContext: ApplicationContext) =
        MessageDispatcherServlet().run {
            setApplicationContext(applicationContext)
            isTransformWsdlLocations = true
            ServletRegistrationBean(this, "/api/*")
        }

    @Bean(name = ["identity"])
    fun defaultWsdl11Definition(identitySchema: XsdSchema) =
        DefaultWsdl11Definition().apply {
            setPortTypeName("IdentityManagementPort")
            setLocationUri("/api")
            setTargetNamespace(IdentityManagementEndpoint.NAMESPACE_URL)
            setSchema(identitySchema)
        }

    @Bean
    fun authenticationSchema() = SimpleXsdSchema(ClassPathResource("identity.xsd"))
}