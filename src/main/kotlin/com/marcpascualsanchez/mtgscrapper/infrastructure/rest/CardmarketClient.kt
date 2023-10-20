package com.marcpascualsanchez.mtgscrapper.infrastructure.rest

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CardmarketClient(@Value("\${cardmarket.url}") url: String) {

    fun getCardFromSellerPage(seller: String, cardName: String): Document {
        return try {
            Jsoup.connect(
                "/$seller/Offers/Singles?name=${encodeCardName(cardName)}",
            ).timeout(TIMEOUT)
                .get()
        } catch (e: Exception) {
            println("timeout for card $cardName for seller $seller")
            throw Exception() // TODO: custom exception
        }
    }

    private fun encodeCardName(cardName: String) = cardName // TODO: is it automatically done?

    companion object {
        const val TIMEOUT = 10_000
    }
}