package com.marcpascualsanchez.mtgscrapper.infrastructure.rest

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class WebDriverManager(
    @Value("\${cardmarket.page_load_timeout}") private val pageLoadTimeout: Long,
) {
    @Bean()
    fun chromeDriver(): ChromeDriver {
        WebDriverManager.chromedriver().setup();
        val chromeDriver = ChromeDriver()
        chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout))
        return chromeDriver
    }
}