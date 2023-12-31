package com.marcpascualsanchez.mtgscrapper.steps

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.marcpascualsanchez.mtgscrapper.BaseApplication
import io.cucumber.datatable.DataTable
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.spring.CucumberContextConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import java.net.URLEncoder
import java.util.*

@CucumberContextConfiguration
class MtgScrapperSteps(
    private val wireMockServer: WireMockServer,
) : BaseApplication() {

    @LocalServerPort
    private var port = 0
    private val testRestTemplate = TestRestTemplate()
    val baseUrl: String
        get() = "http://localhost:$port"
    private var currentResponse = ""

    @Before
    fun before() {
        wireMockServer.resetAll()
    }

    @Given("cardmarket website responses are")
    fun `cardmarket website responses are`(dataTable: DataTable) {
        dataTable.asMaps().forEach {
            wireMockServer.stubFor(
                WireMock
                    .get(WireMock.urlEqualTo(getCardmarketPath(it["seller"]!!, it["cardName"]!!)))
                    .willReturn(
                        ResponseDefinitionBuilder.responseDefinition()
                            .withStatus(HttpStatus.OK.value())
                            .withBody(parseFileToString("response/cardmarket/${it["response"]!!}"))
                    )
            )
        }
    }

    @When("a POST is requested with body {string}")
    fun `a POST is received with body`(fileName: String) {
        currentResponse = baseCall(
            HttpEntity(parseFileToString("request/$fileName"), getDefaultHeaders()),
            "$baseUrl/api/v1/cards-list/best-offers",
            HttpMethod.POST
        )
    }

    @Then("the response is a csv matching {string}")
    fun `the response is a csv matching`(fileName: String) {
        assertThat(currentResponse).isEqualTo(parseFileToString("expected/$fileName"))
    }

    private fun baseCall(
        entity: HttpEntity<*>?,
        path: String,
        httpMethod: HttpMethod
    ): String {
        val response = testRestTemplate.exchange(path, httpMethod, entity, String::class.java)
        return response.body!!
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

    private fun getCardmarketPath(seller: String, cardName: String) =
        "/Users/$seller/Offers/Singles?name=${URLEncoder.encode(cardName, "UTF-8")}&sortBy=price_asc"
}