package com.marcpascualsanchez.mtgscrapper.api.controller

import com.marcpascualsanchez.mtgscrapper.api.request.CardsListEvaluationRequest
import com.marcpascualsanchez.mtgscrapper.api.response.CardListEvaluationResponse
import com.marcpascualsanchez.mtgscrapper.api.service.CardListEvaluatorService
import com.marcpascualsanchez.mtgscrapper.domain.entity.CardEvaluation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/cards-list")
class CardListController(
    private val cardListEvaluator: CardListEvaluatorService
) {

    @PostMapping("/evaluate")
    fun evaluate(
        @Valid @RequestBody request: CardsListEvaluationRequest,
    ): CardListEvaluationResponse {
        return listOf(
            CardEvaluation(
                0.99,
                "sellername",
                "cardname 1"
            ),
            CardEvaluation(
                3.50,
                "sellername",
                "cardname 2"
            ),
        )
    }
}