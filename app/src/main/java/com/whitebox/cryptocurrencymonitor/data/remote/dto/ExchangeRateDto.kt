package com.whitebox.cryptocurrencymonitor.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ExchangeRateDto(
    @SerializedName("asset_id_base")
    val assetIdBase: String,
    @SerializedName("asset_id_quote")
    val assetIdQuote: String,
    @SerializedName("rate")
    val rate: Double,
    @SerializedName("time")
    val time: String
)
