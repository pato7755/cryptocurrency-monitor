package com.whitebox.cryptocurrencymonitor.data.repository

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAsset
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAssetIcon
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainExchangeRate
import com.whitebox.cryptocurrencymonitor.data.mapper.toLocalAsset
import com.whitebox.cryptocurrencymonitor.data.mapper.toLocalExchangeRate
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
) : CryptocurrencyRepository {

    override suspend fun getAssets(): Flow<WorkResult<List<Asset>>> = flow {
        var localAssets = dao.getAllAssets()
        if (localAssets.isNotEmpty()) {
            emit(WorkResult.Loading(localAssets.map { it.toDomainAsset() }))
        } else {
            emit(WorkResult.Loading())
        }

        try {
            val remoteAssets = api.getAssets().map { it.toDomainAsset() }
            dao.upsertAssets(remoteAssets.map { it.toLocalAsset() })
            localAssets = dao.getAllAssets()
            emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
        } catch (e: HttpException) {
            emit(WorkResult.Error(
                message = e.message ?: "An error occurred while fetching exchange rate",
                data = localAssets.map { it.toDomainAsset() }
            ))
        } catch (e: IOException) {
            emit(WorkResult.Error(
                message = e.message ?: "An error occurred while fetching exchange rate",
                data = localAssets.map { it.toDomainAsset() }
            ))
        }
    }

    override suspend fun getAsset(assetId: String): Flow<WorkResult<Asset?>> = flow {
        var asset = dao.getAssetById(assetId)?.toDomainAsset()
        emit(WorkResult.Loading(asset))

        // if fetch from cache fails, fetch from network
        asset?.let {
            asset = api.getAssetDetails(assetId).toDomainAsset()
        }
        emit(WorkResult.Success(asset))
    }

    override suspend fun getAssetIcons(size: String): Flow<WorkResult<List<AssetIcon?>>> = flow {
        emit(WorkResult.Loading())
        try {
            val remoteAssetIcons = api.getAssetIcons(size).map { it.toDomainAssetIcon() }
            emit(WorkResult.Success(remoteAssetIcons))
        } catch (e: HttpException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching asset icons",
                    data = emptyList()
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching asset icons",
                    data = emptyList()
                )
            )
        }

    }

    override suspend fun getExchangeRate(assetId: String): Flow<WorkResult<ExchangeRate>> = flow {
        var exchangeRate = dao.getExchangeRate(assetId)
        emit(WorkResult.Loading(exchangeRate?.toDomainExchangeRate()))

        try {
            val remoteExchangeRate = api.getExchangeRate(assetId).toDomainExchangeRate()
            dao.upsertExchangeRate(remoteExchangeRate.toLocalExchangeRate())
            exchangeRate = dao.getExchangeRate(assetId)
            exchangeRate?.let {
                emit(WorkResult.Success(it.toDomainExchangeRate()))
            } ?: run {
                emit(
                    WorkResult.Error(
                        message = "An error occurred while fetching exchange rate",
                        data = null
                    )
                )
            }
        } catch (e: HttpException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching assets",
                    data = exchangeRate?.toDomainExchangeRate()
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching assets",
                    data = exchangeRate?.toDomainExchangeRate()
                )
            )
        }
    }

}
