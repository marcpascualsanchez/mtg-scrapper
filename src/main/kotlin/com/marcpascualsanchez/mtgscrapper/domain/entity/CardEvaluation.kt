package com.marcpascualsanchez.mtgscrapper.domain.entity

data class CardEvaluation(
    val minPrice: Double,
    val minPriceSeller: String,
    val cardVersionName: String,
    // TODO: foils
)