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
        return mapToResponse(
            cardListEvaluator.evaluate(
                parseCardList(request.rawList), request.sellers
            )
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

    private fun mapToResponse(cardEvaluations: List<CardEvaluation>) =
        StreamingResponseBody { outputStream ->
            val csvPrinter =
                CSVPrinter(
                    OutputStreamWriter(outputStream),
                    CSVFormat.DEFAULT
                        .withHeader(*HEADERS_RESPONSE_LINE)
                        .withRecordSeparator("\n")
                )

            csvPrinter.printRecord(computeGeneralData(cardEvaluations))
            cardEvaluations.forEach { csvPrinter.printRecord(mapToRecord(it)) }

            csvPrinter.close()
        }

    private fun computeGeneralData(cardEvaluations: List<CardEvaluation>): List<String?> {
        val foundCards = cardEvaluations.filterIsInstance(FoundCardEvaluation::class.java)
        return listOf(
            null,
            null,
            null,
            foundCards.size.toString(),
            (cardEvaluations.size - foundCards.size).toString(),
            foundCards.sumOf { it.minPrice }.toString()
        )
    }

    private fun mapToRecord(evaluation: CardEvaluation) = when (evaluation) {
        is FoundCardEvaluation -> listOf(evaluation.cardVersionName, evaluation.minPrice, evaluation.minPriceSeller)
        is NotFoundCardEvaluation -> listOf(evaluation.cardVersionName, DEFAULT_EMPTY_LINE, DEFAULT_EMPTY_LINE)
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
        const val DEFAULT_EMPTY_LINE = "N/A"
    }

}
