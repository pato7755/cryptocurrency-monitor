package com.whitebox.cryptocurrencymonitor.domain.repository

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface CryptocurrencyRepository {

    suspend fun getAssets(): Flow<WorkResult<List<Asset>>>

    suspend fun getAsset(assetId: String): Asset

    suspend fun getAssetIcons(size: String): List<AssetIcon>

    suspend fun getExchangeRate(assetId: String): ExchangeRate

}
