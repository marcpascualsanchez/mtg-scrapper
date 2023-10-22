package com.marcpascualsanchez.mtgscrapper.initializers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseTransformer
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent


class WiremockInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val wmServer = WireMockServer(
            WireMockConfiguration().dynamicPort()
                .extensions(ConnectionCloseExtension())
        )
        wmServer.start()

        applicationContext.beanFactory.registerSingleton("wireMockServer", wmServer)

        applicationContext.addApplicationListener { applicationEvent: ApplicationEvent? ->
            if (applicationEvent is ContextClosedEvent) {
                wmServer.stop()
            }
        }

        TestPropertyValues
            .of("wiremock.port=" + wmServer.port())
            .applyTo(applicationContext)
    }
}

class ConnectionCloseExtension : ResponseTransformer() {
    override fun transform(
        request: Request?,
        response: Response,
        files: FileSource?,
        parameters: Parameters?
    ): Response {
        return Response.Builder
            .like(response)
            .headers(
                HttpHeaders.copyOf(response.headers)
                    .plus(HttpHeader("Connection", "Close"))
            )
            .build()
    }

    override fun getName(): String {
        return "ConnectionCloseExtension"
    }
}
