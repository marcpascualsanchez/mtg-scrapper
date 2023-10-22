package com.marcpascualsanchez.mtgscrapper.api.service

import com.marcpascualsanchez.mtgscrapper.domain.entity.Card
import com.marcpascualsanchez.mtgscrapper.domain.entity.CardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.FoundCardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.NotFoundCardEvaluation
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardFound
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardNotFound
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardmarketScrapperService
import org.springframework.stereotype.Service

@Service
class CardListEvaluatorService(
    private val cardmarketScrapperService: CardmarketScrapperService,
) {
    fun evaluate(cardList: List<Card>, sellers: List<String>): List<CardEvaluation> {
        return cardList.map { card ->
            val cardsAtSale = fetchCardsBySeller(sellers, card)
            val bestOffer = cardsAtSale.minByOrNull { it.price }
            if (bestOffer == null) {
                NotFoundCardEvaluation(card.name)
            } else {
                FoundCardEvaluation(
                    minPrice = bestOffer.price,
                    minPriceSeller = bestOffer.seller,
                    cardVersionName = card.name, // TODO: get the scrapped name, not the searched name
                )
            }
        }
    }

    private fun fetchCardsBySeller(
        sellers: List<String>,
        card: Card
    ) = sellers.mapNotNull { seller ->
        when (val cardFoundBySeller = cardmarketScrapperService.getCardBySeller(seller, card.name)) {
            is CardFound -> cardFoundBySeller
            is CardNotFound -> null
            // TODO: what to do when card is not found? seal class for evaluations?
        }
    }
}