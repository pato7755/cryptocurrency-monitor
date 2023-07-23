package com.whitebox.cryptocurrencymonitor.data.repository

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAsset
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAssetIcon
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainExchangeRate
import com.whitebox.cryptocurrencymonitor.data.mapper.toLocalAsset
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CryptocurrencyRepositoryImpl @Inject constructor(
    private val api: AssetApi,
    private val dao: CryptocurrencyDao,

): CryptocurrencyRepository {

    override suspend fun getAssets(): Flow<WorkResult<List<Asset>>> = flow {
        val localAssets = dao.getAllAssets()
        emit(WorkResult.Loading(localAssets.map { it.toDomainAsset() }))

        try {
            if (localAssets.isNotEmpty()) {
                emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
            } else {
                val remoteAssets = api.getAssets().map { it.toDomainAsset() }
                dao.upsertAssets(remoteAssets.map { it.toLocalAsset() })
                emit(WorkResult.Success(remoteAssets))
            }
        } catch (e: HttpException) {
            emit(WorkResult.Error(
                message = e.message ?: "An error occurred while fetching assets",
                data = localAssets.map { it.toDomainAsset() }
            ))
        } catch (e: IOException) {
            emit(WorkResult.Error(
                message = e.message ?: "An error occurred while fetching assets",
                data = localAssets.map { it.toDomainAsset() }
            ))
        }
    }

    override suspend fun getAsset(assetId: String): Asset {
        return api.getAssetDetails(assetId).toDomainAsset()
    }

    override suspend fun getAssetIcons(size: String): List<AssetIcon> {
        return api.getAssetIcons(size).map { it.toDomainAssetIcon() }
    }

    override suspend fun getExchangeRate(assetId: String): ExchangeRate {
        return api.getExchangeRate(assetId).toDomainExchangeRate()
    }

}
