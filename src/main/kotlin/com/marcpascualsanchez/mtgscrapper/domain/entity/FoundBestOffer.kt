package com.marcpascualsanchez.mtgscrapper.domain.entity

sealed class BestOffer(
    open val cardVersionName: String,
    open val amount: Int,
)
data class FoundBestOffer(
    override val cardVersionName: String,
    override val amount: Int,
    val minPrice: Double,
    val minPriceSeller: String,
): BestOffer(cardVersionName, amount)

data class NotFoundOffer(
    override val cardVersionName: String,
    override val amount: Int,
): BestOffer(cardVersionName, amount)