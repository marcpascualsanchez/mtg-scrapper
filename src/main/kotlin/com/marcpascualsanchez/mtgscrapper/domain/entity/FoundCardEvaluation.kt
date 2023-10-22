package com.marcpascualsanchez.mtgscrapper.domain.entity

sealed class CardEvaluation(
    open val cardVersionName: String,
)
data class FoundCardEvaluation(
    override val cardVersionName: String,
    val minPrice: Double,
    val minPriceSeller: String,
    // TODO: foils
): CardEvaluation(cardVersionName)

data class NotFoundCardEvaluation(
    override val cardVersionName: String,
): CardEvaluation(cardVersionName)