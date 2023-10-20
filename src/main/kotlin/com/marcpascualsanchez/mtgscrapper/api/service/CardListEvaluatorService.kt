package com.marcpascualsanchez.mtgscrapper.api.service

import com.marcpascualsanchez.mtgscrapper.domain.entity.Card
import com.marcpascualsanchez.mtgscrapper.domain.entity.CardEvaluation
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardmarketScrapperService
import org.springframework.stereotype.Service

@Service
class CardListEvaluatorService(
    private val cardmarketScrapperService: CardmarketScrapperService,
) {
    fun evaluate(cardList: List<Card>, sellers: List<String>): List<CardEvaluation> {
        return cardList.map { card ->
            val cardsAtSale = sellers.map { seller ->
                cardmarketScrapperService.getCardBySeller(seller, card.name)
            }
            val bestOffer = cardsAtSale.minBy { it.price }
            CardEvaluation(
                minPrice = bestOffer.price,
                minPriceSeller = bestOffer.seller,
                cardVersionName = card.name, // TODO: get the scrapped name, not the searched name
            )
        }
    }
}