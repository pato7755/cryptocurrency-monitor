package com.whitebox.cryptocurrencymonitor.domain.repository

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface CryptocurrencyRepository {

    suspend fun getAssets(): Flow<WorkResult<List<Asset>>>

    suspend fun getAsset(assetId: String): Flow<WorkResult<Asset?>>

    suspend fun getAssetIcons(size: String): Flow<WorkResult<List<AssetIcon?>>>

    suspend fun getExchangeRate(assetId: String): Flow<WorkResult<ExchangeRate>>

    suspend fun getFavouriteAssets(): Flow<WorkResult<List<Asset>>>

    suspend fun addFavouriteAsset(assetId: String)

    suspend fun removeFavouriteAsset(assetId: String)

}
