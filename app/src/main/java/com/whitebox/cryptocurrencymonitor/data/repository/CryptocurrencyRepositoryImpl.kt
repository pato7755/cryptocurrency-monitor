package com.whitebox.cryptocurrencymonitor.data.repository

import android.database.SQLException
import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity
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
import com.whitebox.cryptocurrencymonitor.util.HttpErrorParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class CryptocurrencyRepositoryImpl @Inject constructor(
    private val api: AssetApi,
    private val dao: CryptocurrencyDao,
    private val httpErrorParser: HttpErrorParser
) : CryptocurrencyRepository {

    override suspend fun getAssets(fetchFromRemote: Boolean): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        var localAssets: List<AssetEntity> = emptyList()
        try {
            localAssets = dao.getAllAssets()
        } catch (ex: SQLException) {
            Timber.tag("CryptocurrencyRepositoryImpl").e("getAssets: SQLException - %s", ex.message)
        } catch (ex: Exception) {
            Timber.tag("CryptocurrencyRepositoryImpl").e("getAssets: Exception - %s", ex.message)
        }

        if (!fetchFromRemote) {
            if (localAssets.isNotEmpty()) {
                emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
            } else {
                emit(WorkResult.Error(
                    message = "No data found",
                    data = localAssets.map { it.toDomainAsset() }
                ))
            }
            return@flow
        }

        try {
            if (localAssets.isNotEmpty()) {
                emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
            }
            val remoteAssets = api.getAssets()
                .filter { it.typeIsCrypto == 1 }
                .map { it.toDomainAsset() }
            dao.upsertAssets(remoteAssets.map { it.toLocalAsset() })
            val updatedLocalAssets = dao.getAllAssets()
            emit(WorkResult.Success(updatedLocalAssets.map { it.toDomainAsset() }))
        } catch (e: HttpException) {
            val errorResponse = httpErrorParser.parseResponseBody(
                e.response()?.errorBody()?.string()
            )
            emit(
                WorkResult.Error(
                    message = errorResponse?.error ?: "An error occurred while fetching assets",
                    data = localAssets.map { it.toDomainAsset() }
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching assets",
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
        } catch (ex: SQLException) {
            Timber.tag("CryptocurrencyRepositoryImpl").e("getAsset: SQLException - %s", ex.message)
        } catch (ex: Exception) {
            Timber.tag("CryptocurrencyRepositoryImpl").e("getAsset: Exception - %s", ex.message)
        }

        if (!fetchFromRemote) {
            asset?.let {
                emit(WorkResult.Success(asset))
                Timber.tag("CryptocurrencyRepositoryImpl").e("getAsset: %s", it)
            } ?: emit(
                WorkResult.Error(
                    message = "No data found"
                )
            )
            return@flow
        }

        try {
            asset?.let { emit(WorkResult.Success(asset)) }

            val remoteAsset = api.getAssetDetails(assetId).map { it.toDomainAsset() }.first()
            dao.upsertAssets(listOf(remoteAsset.toLocalAsset()))
            val updatedLocalAsset = dao.getAssetById(assetId = assetId)
            emit(WorkResult.Success(updatedLocalAsset?.toDomainAsset()))
        } catch (e: HttpException) {
            val errorResponse = httpErrorParser.parseResponseBody(
                e.response()?.errorBody()?.string()
            )
            emit(
                WorkResult.Error(
                    message = errorResponse?.error
                        ?: "An error occurred while fetching asset details",
                    data = asset
                )
            )
        } catch (e: IOException) {
            Timber.tag("CryptocurrencyRepositoryImpl").e("io error: %s", e.message)
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
        var remoteAssetIcons: List<AssetIcon> = emptyList()
        try {
            remoteAssetIcons = api.getAssetIcons(size).map { it.toDomainAssetIcon() }
            emit(WorkResult.Success(remoteAssetIcons))
        } catch (e: HttpException) {
            val errorResponse = httpErrorParser.parseResponseBody(
                e.response()?.errorBody()?.string()
            )
            emit(
                WorkResult.Error(
                    message = errorResponse?.error
                        ?: "An error occurred while fetching asset icons",
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

        /** save icon URLs to DB
         * There are a few better approaches to this considering the huge number of records to update.
         * 1. Use a background worker to update the records
         * 2. Use a transaction to update the records
         * 3. Use a batch update
         */
        remoteAssetIcons.forEach { assetIcon ->
            setAssetIconUrl(assetId = assetIcon.assetId, iconUrl = assetIcon.url)
        }
    }

    override suspend fun getExchangeRate(
        assetId: String,
        fetchFromRemote: Boolean
    ): Flow<WorkResult<ExchangeRate>> = flow {
        emit(WorkResult.Loading())
        var localExchangeRate: ExchangeRateEntity? = null
        try {
            localExchangeRate = dao.getExchangeRate(assetId)

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
        } catch (ex: SQLException) {
            Timber.tag("CryptocurrencyRepositoryImpl")
                .e("getExchangeRate: SQLException - %s", ex.message)
        } catch (ex: Exception) {
            Timber.tag("CryptocurrencyRepositoryImpl")
                .e("getExchangeRate: Exception - %s", ex.message)
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
            val errorResponse = httpErrorParser.parseResponseBody(
                e.response()?.errorBody()?.string()
            )
            emit(
                WorkResult.Error(
                    message = errorResponse?.error ?: e.message(),
                    data = localExchangeRate?.toDomainExchangeRate()
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while fetching exchange rate",
                    data = localExchangeRate?.toDomainExchangeRate()
                )
            )
        }
    }

    override suspend fun getFavouriteAssets(): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        try {
            val localAssets = dao.getAllAssets().filter { it.isFavourite }
            emit(WorkResult.Success(localAssets.map { it.toDomainAsset() }))
        } catch (ex: SQLException) {
            Timber.tag("CryptocurrencyRepositoryImpl")
                .e("getFavouriteAssets: SQLException - %s", ex.message)
        } catch (ex: Exception) {
            Timber.tag("CryptocurrencyRepositoryImpl")
                .e("getFavouriteAssets: Exception - %s", ex.message)
        }

    }

    override suspend fun addFavouriteAsset(assetId: String): Boolean {
        try {
            dao.getAssetById(assetId = assetId)?.let {
                dao.addFavouriteAsset(assetId = assetId)
                return true
            }
        } catch (e: IOException) {
            Timber.tag("Add favourite").d(e.message.toString())
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
            Timber.tag("Remove favourite").d(e.message.toString())
        }
        return false
    }

    override suspend fun searchAssets(searchString: String): Flow<WorkResult<List<Asset>>> = flow {
        emit(WorkResult.Loading())
        try {
            val localAssets = dao.searchAssets(searchString)
                .filter { it.typeIsCrypto == 1 }
                .map { it.toDomainAsset() }
            emit(WorkResult.Success(localAssets))
        } catch (e: HttpException) {
            val errorResponse = httpErrorParser.parseResponseBody(
                e.response()?.errorBody()?.string()
            )
            emit(
                WorkResult.Error(
                    message = errorResponse?.error ?: "An error occurred while searching for assets"
                )
            )
        } catch (e: IOException) {
            emit(
                WorkResult.Error(
                    message = e.message ?: "An error occurred while searching for assets"
                )
            )
        }
    }

    override fun setAssetIconUrl(assetId: String, iconUrl: String): Boolean {
        try {
            dao.updateAssetIconUrlIfNull(assetId = assetId, iconUrl = iconUrl)
            return true
        } catch (e: IOException) {
            Timber.tag("Set icon url - $assetId").d(e.message.toString())
        }
        return false
    }

}
