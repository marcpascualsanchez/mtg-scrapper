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
    val injectScript = String(javaClass.getResourceAsStream(JS_FLAG_VALID_PRICES_FILE).readAllBytes())

    fun searchAndPrepareForScrapping(seller: String, cardName: String): Document? {
        return try {
            chromeDriver.get(buildUrl(seller, cardName))
            flagMatchingCardElements(cardName)
            Thread.sleep(randomSleep())
            Jsoup.parse(chromeDriver.pageSource)
        } catch (e: Exception) {
            println("error finding card $cardName for seller $seller")
            println(e)
            null
        }
    }

    private fun flagMatchingCardElements(cardName: String) {
        val scriptExecution = "flagValidPrices(`$cardName`)"
        chromeDriver.executeScript("$injectScript $scriptExecution")
    }

    private fun buildUrl(seller: String, cardName: String) =
        "$url/Users/$seller/Offers/Singles?name=${URLEncoder.encode(cardName, "UTF-8")}&sortBy=price_asc"

    private fun randomSleep() = (maxSleepTime..minSleepTime).random()

    companion object {
        const val JS_FLAG_VALID_PRICES_FILE = "/js/flagValidPrices.js"
    }
}