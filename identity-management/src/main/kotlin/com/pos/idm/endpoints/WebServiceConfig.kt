package com.pos.idm.endpoints

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
class WebServiceConfig : WsConfigurerAdapter() {
    @Bean
    fun messageDispatcherServlet(applicationContext: ApplicationContext) =
        MessageDispatcherServlet().run {
            setApplicationContext(applicationContext)
            isTransformWsdlLocations = true
            ServletRegistrationBean(this, "/api/*")
        }

    @Bean(name = ["idm"])
    fun defaultWsdl11Definition(idmSchema: XsdSchema) =
        DefaultWsdl11Definition().apply {
            setPortTypeName("IdentityManagementPort")
            setLocationUri("/api")
            setTargetNamespace(IdentityManagementEndpoint.NAMESPACE_URL)
            setSchema(idmSchema)
        }

    @Bean
    fun authenticationSchema() = SimpleXsdSchema(ClassPathResource("idm.xsd"))
}