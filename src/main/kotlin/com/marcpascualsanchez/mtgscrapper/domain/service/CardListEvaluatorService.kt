package com.marcpascualsanchez.mtgscrapper.domain.service

import com.marcpascualsanchez.mtgscrapper.domain.entity.BestOffer
import com.marcpascualsanchez.mtgscrapper.domain.entity.Card
import com.marcpascualsanchez.mtgscrapper.domain.entity.FoundBestOffer
import com.marcpascualsanchez.mtgscrapper.domain.entity.NotFoundOffer
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardFound
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardNotFound
import com.marcpascualsanchez.mtgscrapper.infrastructure.service.CardmarketScrapperService
import org.springframework.stereotype.Service

@Service
class CardListEvaluatorService(
    private val cardmarketScrapperService: CardmarketScrapperService,
) {
    fun evaluateBestOffers(cardList: List<Card>, sellers: List<String>): List<BestOffer> {
        return cardList.flatMap { card ->
            combineBestOffers(fetchCardsBySeller(sellers, card), card)
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

    private fun combineBestOffers(
        cardsAtSale: List<CardFound>,
        evaluatedCard: Card
    ): List<BestOffer> {
        val bestOffer = cardsAtSale.minByOrNull { it.price }
            ?: return listOf(mapToNotFoundOffer(evaluatedCard))
        val isEnoughAmount = evaluatedCard.amount <= bestOffer.amount
        val combination = mutableListOf<BestOffer>(mapToFoundBestOffer(bestOffer, evaluatedCard, isEnoughAmount))
        if (!isEnoughAmount) {
            val remainingSellersCards = cardsAtSale.filterNot { it.seller == bestOffer.seller }
            val remainingEvaluatedCard = evaluatedCard.copy(amount = evaluatedCard.amount - bestOffer.amount)
            combination.addAll(
                combineBestOffers(remainingSellersCards, remainingEvaluatedCard)
            )
        }
        return combination
    }

    private fun mapToFoundBestOffer(
        bestOffer: CardFound,
        evaluatedCard: Card,
        isEnoughAmount: Boolean
    ) = FoundBestOffer(
        minPrice = bestOffer.price,
        minPriceSeller = bestOffer.seller,
        cardVersionName = evaluatedCard.name,
        amount = if (isEnoughAmount) evaluatedCard.amount else bestOffer.amount,
    )

    private fun mapToNotFoundOffer(evaluatedCard: Card) =
        NotFoundOffer(evaluatedCard.name, evaluatedCard.amount)
}