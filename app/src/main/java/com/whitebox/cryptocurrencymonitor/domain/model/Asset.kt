package com.whitebox.cryptocurrencymonitor.domain.model


data class Asset(
    val assetId: String,
    val idIcon: String,
    val name: String,
    val typeIsCrypto: Int
)

