package com.marcpascualsanchez.mtgscrapper.infrastructure.service

import com.marcpascualsanchez.mtgscrapper.infrastructure.rest.CardmarketClient
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class CardmarketScrapperService(
    private val cardmarketClient: CardmarketClient
) {
    fun getCardBySeller(seller: String, cardName: String): CardAtSale {
        val page = cardmarketClient.getCardFromSellerPage(seller, cardName) ?: return CardNotFound
        return if (scrapNotFoundMessage(page) != null) {
            CardNotFound
        } else {
            CardFound(
                seller,
                scrapCheaperCard(page),
            )
        }
    }

    private fun scrapNotFoundMessage(htmlPage: Document) = htmlPage.body().selectFirst(NO_RESULTS_CSS_SELECTOR)

    private fun scrapCheaperCard(htmlPage: Document): Double {
        return htmlPage
            .body()
            .select(PRICE_CSS_SELECTOR)
            .minOf { parsePrice(it.text()) }
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
        const val NO_RESULTS_CSS_SELECTOR = ".noResults"
        const val PRICE_CSS_SELECTOR = ".table-body > * .price-container"
    }
}

sealed class CardAtSale
data class CardFound(
    val seller: String,
    val price: Double,
    //val amount: Int
) : CardAtSale()

object CardNotFound : CardAtSale()