package com.marcpascualsanchez.mtgscrapper.infrastructure.service

sealed class CardAtSale
data class CardFound(
    val seller: String,
    val price: Double,
    //val amount: Int
) : CardAtSale()

object CardNotFound : CardAtSale()