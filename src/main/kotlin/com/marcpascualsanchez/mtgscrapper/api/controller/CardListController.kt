package com.marcpascualsanchez.mtgscrapper.api.controller

import com.marcpascualsanchez.mtgscrapper.api.request.CardsListEvaluationRequest
import com.marcpascualsanchez.mtgscrapper.domain.entity.Card
import com.marcpascualsanchez.mtgscrapper.domain.entity.BestOffer
import com.marcpascualsanchez.mtgscrapper.domain.entity.FoundBestOffer
import com.marcpascualsanchez.mtgscrapper.domain.entity.NotFoundOffer
import com.marcpascualsanchez.mtgscrapper.domain.service.CardListEvaluatorService
import jakarta.validation.Valid
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.OutputStreamWriter

@RestController
@RequestMapping("/api/v1/cards-list")
class CardListController(
    private val cardListEvaluator: CardListEvaluatorService
) {

    @PostMapping("/best-offers")
    fun evaluate(
        @Valid @RequestBody request: CardsListEvaluationRequest,
    ): StreamingResponseBody {
        val requestedCards = parseCardList(request.rawList)
        return mapToResponse(
            cardListEvaluator.evaluateBestOffers(
                requestedCards, request.sellers
            ),
            requestedCards
        )
    }

    private fun parseCardList(rawCardList: String): List<Card> =
        rawCardList
            .trimIndent()
            .split("\n")
            .map {
                val parts = it.split(" ", limit = 2)
                Card(amount = parts[0].toIntOrNull() ?: 0, name = parts[1].trim())
            }

    private fun mapToResponse(bestOffers: List<BestOffer>, requestedCards: List<Card>) =
        StreamingResponseBody { outputStream ->
            val csvPrinter =
                CSVPrinter(
                    OutputStreamWriter(outputStream),
                    CSVFormat.DEFAULT
                        .withHeader(*HEADERS_RESPONSE_LINE)
                        .withRecordSeparator("\n")
                )

            csvPrinter.printRecord(computeGeneralData(bestOffers, requestedCards))
            bestOffers.forEach { csvPrinter.printRecord(mapToRecord(it)) }

            csvPrinter.close()
        }

    private fun computeGeneralData(bestOffers: List<BestOffer>, requestedCards: List<Card>): List<String?> {
        val foundCards = bestOffers.filterIsInstance(FoundBestOffer::class.java)
        val foundCardsAmount = foundCards.sumOf { it.amount }
        return listOf(
            DEFAULT_EMPTY,
            DEFAULT_EMPTY,
            DEFAULT_EMPTY,
            foundCardsAmount.toString(),
            (requestedCards.sumOf { it.amount } - foundCardsAmount).toString(),
            "%.2f".format(foundCards.sumOf { it.minPrice * it.amount })
        )
    }

    private fun mapToRecord(evaluation: BestOffer) = when (evaluation) {
        is FoundBestOffer -> listOf(
            evaluation.cardVersionName,
            evaluation.minPrice,
            evaluation.minPriceSeller,
            evaluation.amount
        )

        is NotFoundOffer -> listOf(evaluation.cardVersionName, DEFAULT_UNKNOWN, DEFAULT_UNKNOWN, DEFAULT_EMPTY, evaluation.amount)
    }

    companion object {
        val HEADERS_RESPONSE_LINE =
            listOf(
                "card name",
                "cheapest price",
                "seller",
                "found cards",
                "not found cards",
                "list total price"
            ).toTypedArray()
        const val DEFAULT_UNKNOWN = "N/A"
        val DEFAULT_EMPTY = null
    }

}
