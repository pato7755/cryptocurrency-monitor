package com.whitebox.cryptocurrencymonitor.domain.model


data class Asset(
    val assetId: String,
    val name: String,
    val typeIsCrypto: Int,
    val iconUrl: String? = null,
    val isFavourite: Boolean = false,
    val priceUsd: String
)
