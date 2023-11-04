package com.marcpascualsanchez.mtgscrapper.domain.entity.service

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
        return cardList.flatMap { card ->
            bestOffersCombination(fetchCardsBySeller(sellers, card), card)
        }
    }

    private fun bestOffersCombination(
        cardsAtSale: List<CardFound>,
        card: Card
    ): List<CardEvaluation> {
        val bestOffer = cardsAtSale.minByOrNull { it.price }
        return if (bestOffer == null) {
            listOf(NotFoundCardEvaluation(card.name))
        } else {
            val isEnoughAmount = card.amount <= bestOffer.amount
            val res = mutableListOf<CardEvaluation>(
                FoundCardEvaluation(
                    minPrice = bestOffer.price,
                    minPriceSeller = bestOffer.seller,
                    cardVersionName = card.name,
                    amount = if(isEnoughAmount) card.amount else bestOffer.amount,
                )
            )
            if (!isEnoughAmount) {
                res.addAll(
                    bestOffersCombination(
                        cardsAtSale.filterNot { it.seller == bestOffer.seller },
                        card.copy(amount = card.amount - bestOffer.amount)
                    )
                )
            }
            res
        }
    }

    private fun fetchCardsBySeller(
        sellers: List<String>,
        card: Card
    ) = sellers.mapNotNull { seller ->
        when (val cardFoundBySeller = cardmarketScrapperService.getCardBySeller(seller, card.name)) {
            is CardFound -> cardFoundBySeller
            is CardNotFound -> null
        }
    }
}