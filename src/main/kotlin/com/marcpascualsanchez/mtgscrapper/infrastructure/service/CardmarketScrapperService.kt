package com.marcpascualsanchez.mtgscrapper.infrastructure.service

import com.marcpascualsanchez.mtgscrapper.infrastructure.rest.CardmarketWebDriver
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class CardmarketScrapperService(
    private val cardmarketWebDriver: CardmarketWebDriver
) {
    fun getCardBySeller(seller: String, cardName: String): CardAtSale {
        val page = cardmarketWebDriver.searchAndPrepareForScrapping(seller, cardName) ?: return CardNotFound
        val cheapestCard = scrapCheaperCard(page) ?: return CardNotFound
        return CardFound(
            seller,
            cheapestCard.price,
            scrapCardAmount(page, cheapestCard.id)
        )
    }

    private fun scrapCheaperCard(htmlPage: Document): CheapestCard? {
        val cheapestPrice = htmlPage.body()
            .select(PRICE_CSS_SELECTOR)
            .minByOrNull { parsePrice(it.text()) }
        return cheapestPrice?.run {
            CheapestCard(
                cheapestPrice.attr(CARD_ID_ATTRIBUTE).toInt(),
                parsePrice(cheapestPrice.text())
            )
        }
    }

    private fun scrapCardAmount(htmlPage: Document, id: Int): Int = htmlPage.body()
        .select(AMOUNT_CSS_SELECTOR_TEMPLATE.format(id))
        .text()
        .toInt()

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
        const val CARD_ID_ATTRIBUTE = "mtg-scrapper-id"
        const val AMOUNT_CSS_SELECTOR_TEMPLATE = ".mtg-scrapper-valid-amount[$CARD_ID_ATTRIBUTE=\"%s\"]"
    }
}