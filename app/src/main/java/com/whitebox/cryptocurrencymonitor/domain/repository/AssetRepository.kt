package com.whitebox.cryptocurrencymonitor.domain.repository

import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate

interface AssetRepository {

    suspend fun getAssets(): List<Asset>

    suspend fun getAsset(assetId: String): Asset

    suspend fun getAssetIcons(assetId: String): AssetIcon

    suspend fun getExchangeRate(assetId: String): ExchangeRate

}
