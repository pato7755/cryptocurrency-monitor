package com.whitebox.cryptocurrencymonitor.data.remote

import com.whitebox.cryptocurrencymonitor.data.local.CryptocurrencyDao
import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity

class FakeLocalDao : CryptocurrencyDao {

    private val assets = mutableListOf<AssetEntity>()
    private val exchangeRates = mutableListOf<ExchangeRateEntity>()

    override fun upsertAssets(assets: List<AssetEntity>) {
        this.assets.addAll(assets)
    }

    override fun addFavouriteAsset(assetId: String) {
        this.assets.find { it.assetId == assetId }?.let {
            val updatedAsset = it.copy(isFavourite = true)
            assets.remove(it)
            assets.add(updatedAsset)
        }
    }

    override fun removeFavouriteAsset(assetId: String) {
        this.assets.find { it.assetId == assetId }?.let {
            val updatedAsset = it.copy(isFavourite = false)
            assets.remove(it)
            assets.add(updatedAsset)
        }
    }

    override suspend fun getFavouriteAssets(): List<AssetEntity> {
        return this.assets.find { it.isFavourite }?.let {
            listOf(it)
        } ?: run {
            emptyList()
        }
    }

    override suspend fun getAssetById(assetId: String): AssetEntity? {
        return this.assets.find { it.assetId == assetId }
    }

    override suspend fun getAllAssets(): List<AssetEntity> {
        return this.assets
    }

    override fun getAssetIconUrl(assetId: String): String? {
        return this.assets.find { it.assetId == assetId }?.iconUrl
    }

    override suspend fun upsertExchangeRate(exchangeRate: ExchangeRateEntity) {
        this.exchangeRates.add(exchangeRate)
    }

    override suspend fun getExchangeRate(assetIdBase: String): ExchangeRateEntity? {
        return exchangeRates.find { it.assetIdBase == assetIdBase }
    }

    override suspend fun searchAssets(searchString: String): List<AssetEntity> {
        return this.assets.filter { it.assetId.contains(searchString) }
    }
}