package com.whitebox.cryptocurrencymonitor.data.repository

import android.util.Log
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

    override suspend fun getAssets(fetchFromRemote: Boolean): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        val localAssets = dao.getAllAssets()
        emit(WorkResult.Loading(localAssets.map { it.toDomainAsset() }))

        if (!fetchFromRemote) {
            emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
            return@flow
        }

        try {
            val remoteAssets = api.getAssets().map { it.toDomainAsset() }
            dao.upsertAssets(remoteAssets.map { it.toLocalAsset() })
            val updatedLocalAssets = dao.getAllAssets()
            emit(WorkResult.Success(updatedLocalAssets.map { it.toDomainAsset() }))
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

    override suspend fun getAsset(
        assetId: String,
        fetchFromRemote: Boolean
    ): Flow<WorkResult<Asset?>> = flow {
        var asset = dao.getAssetById(assetId)?.toDomainAsset()
        emit(WorkResult.Loading(asset))

        if (!fetchFromRemote) {
            emit(WorkResult.Success(asset))
            return@flow
        }

        try {
            // if fetch from cache fails, fetch from network
            asset?.let {
                asset = api.getAssetDetails(assetId).map { it.toDomainAsset() }.first()
                emit(WorkResult.Success(asset))
            }
        } catch (e: HttpException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching asset details",
                    data = asset
                )
            )
        } catch (e: IOException) {
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
        var exchangeRate = dao.getExchangeRate(assetId)
        emit(WorkResult.Loading(exchangeRate?.toDomainExchangeRate()))

        if (!fetchFromRemote) {
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
            return@flow
        }

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

    override suspend fun getFavouriteAssets(): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        try {
            val localAssets = dao.getAllAssets().filter { it.isFavourite ?: false }
            emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
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

    override suspend fun addFavouriteAsset(assetId: String): Boolean {
        try {
            dao.getAssetById(assetId = assetId)?.let {
                dao.addFavouriteAsset(assetId = assetId)
                return true
            }
        } catch (e: IOException) {
            Log.d("fav", e.message.toString())
        }
//        dao.addFavouriteAsset(assetId = assetId)
//        Log.d("fav", dao.getAssetById(assetId = assetId)?.isFavourite.toString())
//        return dao.getAssetById(assetId = assetId)?.isFavourite ?: false
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
//        dao.removeFavouriteAsset(assetId = assetId)
//        Log.d("fav", dao.getAssetById(assetId = assetId)?.isFavourite.toString())
//        return dao.getAssetById(assetId = assetId)?.isFavourite ?: true
    }

    override suspend fun searchAssets(searchString: String): Flow<WorkResult<List<Asset>>>  = flow {
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
