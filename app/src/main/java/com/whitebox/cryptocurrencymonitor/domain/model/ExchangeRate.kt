package com.whitebox.cryptocurrencymonitor.domain.model


data class ExchangeRate(
    val assetIdBase: String,
    val assetIdQuote: String,
    val rate: Double,
    val time: String
)

