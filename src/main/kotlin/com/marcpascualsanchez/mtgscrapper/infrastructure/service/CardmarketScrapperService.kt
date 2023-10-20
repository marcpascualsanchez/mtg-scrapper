package com.marcpascualsanchez.mtgscrapper.infrastructure.service

import com.marcpascualsanchez.mtgscrapper.infrastructure.rest.CardmarketClient
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class CardmarketScrapperService(
    private val cardmarketClient: CardmarketClient
) {
    fun getCardBySeller(seller: String, cardName: String): CardAtSale {
        return CardAtSale(
            seller,
            scrapCheaperCard(cardmarketClient.getCardFromSellerPage(seller, cardName)),
        )
    }

    private fun scrapCheaperCard(htmlPage: Document): Double {
        return 0.99 // TODO: play with cardmarket html in web devtools to get the CSS query selector
    }
}

data class CardAtSale(
    val seller: String,
    val price: Double,
    //val amount: Int
)