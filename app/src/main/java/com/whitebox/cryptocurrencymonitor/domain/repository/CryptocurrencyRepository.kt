package com.whitebox.cryptocurrencymonitor.domain.repository

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface CryptocurrencyRepository {

    suspend fun getAssets(fetchFromRemote: Boolean): Flow<WorkResult<List<Asset>>>

    suspend fun getAsset(
        assetId: String,
        fetchFromRemote: Boolean
    ): Flow<WorkResult<Asset?>>

    suspend fun getAssetIcons(
        size: String
    ): Flow<WorkResult<List<AssetIcon?>>>

    suspend fun getExchangeRate(
        assetId: String,
        fetchFromRemote: Boolean
    ): Flow<WorkResult<ExchangeRate>>

    suspend fun getFavouriteAssets(): Flow<WorkResult<List<Asset>>>

    suspend fun addFavouriteAsset(assetId: String): Boolean

    suspend fun removeFavouriteAsset(assetId: String): Boolean

    suspend fun searchAssets(searchString: String): Flow<WorkResult<List<Asset>>>

    fun setAssetIconUrl(assetId: String, iconUrl: String): Boolean

}
