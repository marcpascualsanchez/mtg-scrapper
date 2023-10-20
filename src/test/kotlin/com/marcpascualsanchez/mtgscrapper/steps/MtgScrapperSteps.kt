package com.marcpascualsanchez.mtgscrapper.steps

import com.marcpascualsanchez.mtgscrapper.BaseApplication
import io.cucumber.java.en.Given
import io.cucumber.spring.CucumberContextConfiguration

@CucumberContextConfiguration
class MtgScrapperSteps : BaseApplication() {

    @Given("a POST is received with body {string}")
    fun `a POST is received with body`(fileName: String) {
        println(fileName)
    }
}