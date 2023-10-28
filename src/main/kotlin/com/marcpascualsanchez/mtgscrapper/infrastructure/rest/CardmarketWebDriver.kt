package com.marcpascualsanchez.mtgscrapper.infrastructure.rest

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class CardmarketWebDriver(
    @Value("\${cardmarket.url}") val url: String,
    @Value("\${cardmarket.max_sleep_ms}") val maxSleepTime: Long,
    @Value("\${cardmarket.min_sleep_ms}") val minSleepTime: Long,
    private val chromeDriver: ChromeDriver,
) {
    val flagValidPriceScriptDeclaration = String(javaClass.getResourceAsStream(JS_SCRIPT_FILE).readAllBytes())

    fun searchCardBySeller(seller: String, cardName: String): Document? {
        return try {
            chromeDriver.get(buildUrl(seller, cardName))
            flagValidPricesElements(cardName)
            Thread.sleep(randomSleep())
            Jsoup.parse(chromeDriver.pageSource)
        } catch (e: Exception) {
            println("error finding card $cardName for seller $seller")
            println(e)
            null
        }
    }

    private fun flagValidPricesElements(cardName: String) {
        // put it somewhere else so this injected feature is understood
        val scriptExecution = "flagValidPrices('$cardName')"
        chromeDriver.executeScript("$flagValidPriceScriptDeclaration $scriptExecution")
    }

    private fun buildUrl(seller: String, cardName: String) =
        "$url/Users/$seller/Offers/Singles?name=${URLEncoder.encode(cardName, "UTF-8")}&sortBy=price_asc"

    private fun randomSleep() = (maxSleepTime..minSleepTime).random()

    companion object {
        const val JS_SCRIPT_FILE = "/js/flagValidPrices.js"
    }
}