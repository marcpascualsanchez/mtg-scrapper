package com.marcpascualsanchez.mtgscrapper.api.request

import io.swagger.v3.oas.annotations.media.Schema

data class CardsListEvaluationRequest(
    @field:Schema(
        description = "MTGGoldfish card list format",
        example = "2 Lightning Bolt\n1 Black Lotus"
    ) val rawList: String,
    @field:Schema(description = "List of seller names")
    val sellers: List<String>,
)
