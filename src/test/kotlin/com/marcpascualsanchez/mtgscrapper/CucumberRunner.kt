package com.marcpascualsanchez.mtgscrapper

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    stepNotifications = true,
    features = ["src/test/resources/features"],
    glue = ["com.marcpascualsanchez.mtgscrapper.steps"],
    plugin = ["pretty"],
    tags = "not @ignored",
)
class CucumberRunner
