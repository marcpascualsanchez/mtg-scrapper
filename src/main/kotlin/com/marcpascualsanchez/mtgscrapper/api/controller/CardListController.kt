package com.marcpascualsanchez.mtgscrapper.api.controller

import com.marcpascualsanchez.mtgscrapper.api.request.CardsListEvaluationRequest
import com.marcpascualsanchez.mtgscrapper.api.service.CardListEvaluatorService
import com.marcpascualsanchez.mtgscrapper.domain.entity.Card
import com.marcpascualsanchez.mtgscrapper.domain.entity.CardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.FoundCardEvaluation
import com.marcpascualsanchez.mtgscrapper.domain.entity.NotFoundCardEvaluation
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

    private fun mapToResponse(cardEvaluations: List<CardEvaluation>): StreamingResponseBody {
        val csvLines = cardEvaluations.map {
            when (it) {
                is FoundCardEvaluation -> listOf(it.cardVersionName, it.minPrice, it.minPriceSeller)
                is NotFoundCardEvaluation -> listOf(it.cardVersionName, DEFAULT_EMPTY_LINE, DEFAULT_EMPTY_LINE)
            }
        }
        return StreamingResponseBody { outputStream ->
            val csvPrinter =
                CSVPrinter(OutputStreamWriter(outputStream), CSVFormat.DEFAULT.withHeader(HEADERS_RESPONSE_LINE))

            for (record in csvLines) {
                csvPrinter.printRecord(record)
            }

            csvPrinter.close()
            // TODO: make this work with something that is not from medieval times
        }
    }

    companion object {
        const val HEADERS_RESPONSE_LINE = "card name,cheapest price,seller"
        const val DEFAULT_EMPTY_LINE = "N/A"
    }

}
