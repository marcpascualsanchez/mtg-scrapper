package com.marcpascualsanchez.mtgscrapper.infrastructure.service

import com.marcpascualsanchez.mtgscrapper.infrastructure.rest.CardmarketWebDriver
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class CardmarketScrapperService(
    private val cardmarketWebDriver: CardmarketWebDriver
) {
    fun getCardBySeller(seller: String, cardName: String): CardAtSale {
        val page = cardmarketWebDriver.searchCardBySeller(seller, cardName) ?: return CardNotFound
        val price = scrapCheaperCardPrice(page) ?: return CardNotFound
        return CardFound(
            seller,
            price,
        )
        // TODO: what if we are flagged as robots and another page appears?
    }

    private fun scrapCheaperCardPrice(htmlPage: Document): Double? {
        return htmlPage
            .body()
            .select(PRICE_CSS_SELECTOR)
            .minOfOrNull { parsePrice(it.text()) }
    }

    private fun parsePrice(htmlText: String): Double {
        try {
            return htmlText
                .replace("€", "")
                .replace(',', '.')
                .toDouble()
        } catch (e: Exception) {
            println("parse of html price failed for $htmlText")
            throw e // TODO: custom exception
        }
    }

    companion object {
        const val PRICE_CSS_SELECTOR = ".mtg-scrapper-valid-price"
    }
}