package com.marcpascualsanchez.mtgscrapper

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MtgScrapperApplication::class])
@ActiveProfiles("test")
@ContextConfiguration()
abstract class BaseApplication
