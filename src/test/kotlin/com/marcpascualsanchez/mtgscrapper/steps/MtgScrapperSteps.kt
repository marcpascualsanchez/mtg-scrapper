package com.marcpascualsanchez.mtgscrapper.steps

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcpascualsanchez.mtgscrapper.BaseApplication
import com.marcpascualsanchez.mtgscrapper.domain.entity.CardEvaluation
import org.springframework.http.HttpMethod
import io.cucumber.java.en.Given
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.util.*

@CucumberContextConfiguration
class MtgScrapperSteps(val jacksonMapper: ObjectMapper) : BaseApplication() {

    @LocalServerPort
    private var port = 0
    private val testRestTemplate = TestRestTemplate()
    val baseUrl: String
        get() = "http://localhost:$port"

    @Given("a POST is received with body {string}")
    fun `a POST is received with body`(fileName: String) {
        baseCall<CardEvaluation>(
            HttpEntity(parseFileToString(fileName), getDefaultHeaders()),
            "$baseUrl/evaluate",
            HttpMethod.POST
        )
    }

    private inline fun <reified T> baseCall(
        entity: HttpEntity<*>?,
        path: String,
        httpMethod: HttpMethod
    ): T {
        val response = testRestTemplate.exchange(path, httpMethod, entity, String::class.java)
        return jacksonMapper.readValue(response.body, T::class.java)
    }

    private fun getDefaultHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
        return headers
    }

    private fun parseFileToString(path: String) = String(
        Objects.requireNonNull(
            javaClass.getResourceAsStream("/data/$path")
        ).readAllBytes()
    )
}