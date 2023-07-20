package com.whitebox.cryptocurrencymonitor.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AssetIconDto(
    @SerializedName("asset_id")
    val assetId: String,
    val url: String
)
