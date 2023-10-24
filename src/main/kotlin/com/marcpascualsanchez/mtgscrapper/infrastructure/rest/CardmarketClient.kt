package com.marcpascualsanchez.mtgscrapper.infrastructure.rest

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class CardmarketClient(
    @Value("\${cardmarket.url}") val url: String,
    @Value("\${cardmarket.max_sleep_ms}") val maxSleepTime: Long,
    @Value("\${cardmarket.min_sleep_ms}") val minSleepTime: Long,
    private val chromeDriver: ChromeDriver,
) {

    fun getCardFromSellerPage(seller: String, cardName: String): Document? {
        return try {
            chromeDriver.get(
                "$url/Users/$seller/Offers/Singles?name=${URLEncoder.encode(cardName, "UTF-8")}",
            )
            Thread.sleep(randomSleep())
            Jsoup.parse(chromeDriver.pageSource)
        } catch (e: Exception) {
            println("error finding card $cardName for seller $seller")
            null
        }
    }

    private fun randomSleep() = (maxSleepTime..minSleepTime).random()
}