package com.marcpascualsanchez.mtgscrapper.infrastructure.rest

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebDriverManager {
    @Bean()
    fun chromeDriver(): ChromeDriver {
        WebDriverManager.chromedriver().setup();
        return ChromeDriver()
    }
}