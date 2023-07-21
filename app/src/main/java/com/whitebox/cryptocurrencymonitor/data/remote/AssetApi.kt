package com.whitebox.cryptocurrencymonitor.data.remote

import com.whitebox.cryptocurrencymonitor.common.Constants
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetIconDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.ExchangeRateDto
import retrofit2.http.GET

interface AssetApi {

    @GET("assets")
    fun getAssets(): List<AssetDto>

    @GET("assets/{asset_id}")
    fun getAssetDetails(assetId: String): AssetDto

    @GET("assets/icons/{size}")
    fun getAssetIcons(size: String): List<AssetIconDto>

    @GET("exchangerate/{baseId}/${Constants.EUR_ASSET_CODE}")
    fun getExchangeRate(baseId: String): ExchangeRateDto

}