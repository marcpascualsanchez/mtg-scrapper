package com.marcpascualsanchez.mtgscrapper.api.controller

import com.marcpascualsanchez.mtgscrapper.api.request.CardsListEvaluationRequest
import com.marcpascualsanchez.mtgscrapper.domain.entity.Card
import com.marcpascualsanchez.mtgscrapper.domain.entity.CardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.FoundCardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.NotFoundCardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.service.CardListEvaluatorService
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

    @PostMapping("/evaluate")
    fun evaluate(
        @Valid @RequestBody request: CardsListEvaluationRequest,
    ): StreamingResponseBody {
        val requestedCards = parseCardList(request.rawList)
        return mapToResponse(
            cardListEvaluator.evaluate(
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

    private fun mapToResponse(cardEvaluations: List<CardEvaluation>, requestedCards: List<Card>) =
        StreamingResponseBody { outputStream ->
            val csvPrinter =
                CSVPrinter(
                    OutputStreamWriter(outputStream),
                    CSVFormat.DEFAULT
                        .withHeader(*HEADERS_RESPONSE_LINE)
                        .withRecordSeparator("\n")
                )

            csvPrinter.printRecord(computeGeneralData(cardEvaluations, requestedCards))
            cardEvaluations.forEach { csvPrinter.printRecord(mapToRecord(it)) }

            csvPrinter.close()
        }

    private fun computeGeneralData(cardEvaluations: List<CardEvaluation>, requestedCards: List<Card>): List<String?> {
        val foundCards = cardEvaluations.filterIsInstance(FoundCardEvaluation::class.java)
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

    private fun mapToRecord(evaluation: CardEvaluation) = when (evaluation) {
        is FoundCardEvaluation -> listOf(
            evaluation.cardVersionName,
            evaluation.minPrice,
            evaluation.minPriceSeller,
            evaluation.amount
        )

        is NotFoundCardEvaluation -> listOf(evaluation.cardVersionName, DEFAULT_UNKNOWN, DEFAULT_UNKNOWN, DEFAULT_EMPTY, evaluation.amount)
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
