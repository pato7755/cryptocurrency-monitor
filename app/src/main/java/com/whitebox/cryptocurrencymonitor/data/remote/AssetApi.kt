package com.whitebox.cryptocurrencymonitor.data.remote

import com.whitebox.cryptocurrencymonitor.common.Constants
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetIconDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.ExchangeRateDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AssetApi {

    @GET("assets")
    suspend fun getAssets(): List<AssetDto>

    @GET("assets/{assetId}")
    suspend fun getAssetDetails(@Path("assetId") assetId: String): AssetDto

    @GET("assets/icons/{size}")
    suspend fun getAssetIcons(@Path("size") size: String): List<AssetIconDto>

    @GET("exchangerate/{baseId}/${Constants.EUR_ASSET_CODE}")
    suspend fun getExchangeRate(@Path("baseId") baseId: String): ExchangeRateDto

}