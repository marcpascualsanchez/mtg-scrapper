package com.marcpascualsanchez.mtgscrapper.api.request

data class CardsListEvaluationRequest(
    val rawList: String,
    val sellers: List<String>,
)
