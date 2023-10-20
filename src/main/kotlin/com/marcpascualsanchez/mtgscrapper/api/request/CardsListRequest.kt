package com.marcpascualsanchez.mtgscrapper.api.request

data class CardsListRequest(
    val rawList: String,
    val sellers: List<String>,
)
