package com.whitebox.cryptocurrencymonitor.data.repository

import android.util.Log
import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
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

    override suspend fun getAssets(fetchFromRemote: Boolean): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        var localAssets: List<AssetEntity> = emptyList()
        Log.d("CryptocurrencyRepositoryImpl", "getAssets: fetchFromRemote: $fetchFromRemote")
        try {
            localAssets = dao.getAllAssets()

            if (!fetchFromRemote) {
                if (localAssets.isNotEmpty()) {
                    emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
                    return@flow
                } else {
                    emit(WorkResult.Error(
                        message = "No data found",
                        data = localAssets.map { it.toDomainAsset() }
                    ))
                    return@flow
                }
            }
        } catch (ex: Exception) {
            Log.e("CryptocurrencyRepositoryImpl", "getAssets: ${ex.message}")
        }

        try {
            if (localAssets.isNotEmpty()) {
                emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
            }
            val remoteAssets = api.getAssets().map { it.toDomainAsset() }
            dao.upsertAssets(remoteAssets.map { it.toLocalAsset() })
            val updatedLocalAssets = dao.getAllAssets()
            emit(WorkResult.Success(updatedLocalAssets.map { it.toDomainAsset() }))
        } catch (e: HttpException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching exchange rate",
                    data = localAssets.map { it.toDomainAsset() }
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching exchange rate",
                    data = localAssets.map { it.toDomainAsset() }
                )
            )
        }
    }

    override suspend fun getAsset(
        assetId: String,
        fetchFromRemote: Boolean
    ): Flow<WorkResult<Asset?>> = flow {
        emit(WorkResult.Loading())
        var asset: Asset? = null
        try {
            asset = dao.getAssetById(assetId)?.toDomainAsset()
        } catch (ex: Exception) {
            Log.e("CryptocurrencyRepositoryImpl", "getAsset: ${ex.message}")
        }

        if (!fetchFromRemote) {
            asset?.let { emit(WorkResult.Success(asset))
                Log.e("CryptocurrencyRepositoryImpl", "getAsset: $it")} ?: emit(
                WorkResult.Error(
                    message = "No data found"
                )
            )
            return@flow
        }

        try {
            // if fetch from cache fails, fetch from network
            asset?.let { emit(WorkResult.Success(asset)) }
            Log.e("CryptocurrencyRepositoryImpl", "fetch from remote")
            asset = api.getAssetDetails(assetId).map { it.toDomainAsset() }.first()
            Log.e("CryptocurrencyRepositoryImpl", "asset: $asset")
            emit(WorkResult.Success(asset))
        } catch (e: HttpException) {
            Log.e("CryptocurrencyRepositoryImpl", "http error: ${e.message}")
            Log.e("CryptocurrencyRepositoryImpl", "http error: ${e.localizedMessage}")
            Log.e("CryptocurrencyRepositoryImpl", "http error: ${e.cause}")
            emit(
                WorkResult.Error(
                    message = e.message + " - An error occurred while fetching asset details",
                    data = asset
                )
            )
        } catch (e: IOException) {
            Log.e("CryptocurrencyRepositoryImpl", "io error: ${e.message}")
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching asset details",
                    data = asset
                )
            )
        }

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

    override suspend fun getExchangeRate(
        assetId: String,
        fetchFromRemote: Boolean
    ): Flow<WorkResult<ExchangeRate>> = flow {
        emit(WorkResult.Loading())
        var localExchangeRate = dao.getExchangeRate(assetId)

        if (!fetchFromRemote) {
            localExchangeRate?.let {
                emit(WorkResult.Success(it.toDomainExchangeRate()))
            } ?: run {
                emit(
                    WorkResult.Error(
                        message = "An error occurred while fetching exchange rate",
                        data = null
                    )
                )
            }
            return@flow
        }

        try {
            localExchangeRate?.let {
                emit(WorkResult.Success(it.toDomainExchangeRate()))
            }
            val remoteExchangeRate = api.getExchangeRate(assetId).toDomainExchangeRate()
            dao.upsertExchangeRate(remoteExchangeRate.toLocalExchangeRate())
            localExchangeRate = dao.getExchangeRate(assetId)
            localExchangeRate?.let {
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
                    data = localExchangeRate?.toDomainExchangeRate()
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching assets",
                    data = localExchangeRate?.toDomainExchangeRate()
                )
            )
        }
    }

    override suspend fun getFavouriteAssets(): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        try {
            val localAssets = dao.getAllAssets().filter { it.isFavourite }
            Log.d("getFavouriteAssets", "localAssets: $localAssets")
            emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
        } catch (e: HttpException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching favourite assets"
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching exchange rate"
                )
            )
        }

    }

    override suspend fun addFavouriteAsset(assetId: String): Boolean {
        try {
            dao.getAssetById(assetId = assetId)?.let {
                dao.addFavouriteAsset(assetId = assetId)
                return true
            }
        } catch (e: IOException) {
            Log.d("fav", e.message.toString())
        }
        return false
    }

    override suspend fun removeFavouriteAsset(assetId: String): Boolean {
        try {
            dao.getAssetById(assetId = assetId)?.let {
                dao.removeFavouriteAsset(assetId = assetId)
                return true
            }
        } catch (e: IOException) {
            Log.d("fav", e.message.toString())
        }
        return false
    }

    override suspend fun searchAssets(searchString: String): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        try {
            val localAssets = dao.searchAssets(searchString).map { it.toDomainAsset() }
            Log.d("dao", localAssets.toString())
            emit(WorkResult.Success(localAssets))
        } catch (e: HttpException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching exchange rate"
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching exchange rate"
                )
            )
        }
    }

}
