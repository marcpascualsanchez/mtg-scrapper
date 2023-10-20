package com.marcpascualsanchez.mtgscrapper.api.controller

import com.marcpascualsanchez.mtgscrapper.api.request.CardsListRequest
import com.marcpascualsanchez.mtgscrapper.api.service.CardListEvaluatorService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/cards-list")
class CardListController(
    private val cardListEvaluator: CardListEvaluatorService
){

    @PostMapping("/evaluate")
    fun evaluate(
        @Valid @RequestBody request: CardsListRequest,
    ) {
        return
    }
}