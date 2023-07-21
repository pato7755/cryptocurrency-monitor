package com.whitebox.cryptocurrencymonitor.data.repository

import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAsset
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainAssetIcon
import com.whitebox.cryptocurrencymonitor.data.mapper.toDomainExchangeRate
import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate
import com.whitebox.cryptocurrencymonitor.domain.repository.AssetRepository
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val api: AssetApi
): AssetRepository {

    override suspend fun getAssets(): List<Asset> {
        return api.getAssets().map { it.toDomainAsset() }
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
