package com.marcpascualsanchez.mtgscrapper.domain.entity

sealed class CardEvaluation(
    open val cardVersionName: String,
    open val amount: Int,
)
data class FoundCardEvaluation(
    override val cardVersionName: String,
    override val amount: Int,
    val minPrice: Double,
    val minPriceSeller: String,
    // TODO: foils
): CardEvaluation(cardVersionName, amount)

data class NotFoundCardEvaluation(
    override val cardVersionName: String,
    override val amount: Int,
): CardEvaluation(cardVersionName, amount)