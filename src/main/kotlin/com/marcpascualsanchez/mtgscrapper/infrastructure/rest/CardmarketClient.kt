package com.marcpascualsanchez.mtgscrapper.infrastructure.rest

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class CardmarketClient(@Value("\${cardmarket.url}") val url: String) {

    fun getCardFromSellerPage(seller: String, cardName: String): Document? {
        return try {
            Jsoup.connect(
                "$url/Users/$seller/Offers/Singles?name=${URLEncoder.encode(cardName, "UTF-8")}",
            )
                .timeout(TIMEOUT)
                .get()
        } catch (e: Exception) {
            println("timeout for card $cardName for seller $seller")
             null
        }
    }

    companion object {
        const val TIMEOUT = 10_000
    }
}