package com.whitebox.cryptocurrencymonitor.data.mapper

import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetIconDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.ExchangeRateDto
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate

fun AssetDto.toDomainAsset(): Asset {
    return Asset(
        assetId = assetId,
        name = name,
        typeIsCrypto = typeIsCrypto
    )
}

fun AssetEntity.toDomainAsset(): Asset {
    return Asset(
        assetId = assetId,
        name = name,
        typeIsCrypto = typeIsCrypto
    )
}

fun Asset.toLocalAsset(): AssetEntity {
    return AssetEntity(
        assetId = assetId,
        name = name,
        typeIsCrypto = typeIsCrypto
    )
}

fun AssetIconDto.toDomainAssetIcon(): AssetIcon {
    return AssetIcon(
        assetId = assetId,
        url = url
    )
}

fun ExchangeRateDto.toDomainExchangeRate(): ExchangeRate {
    return ExchangeRate(
        assetIdBase = assetIdBase,
        assetIdQuote = assetIdQuote,
        rate = rate,
        time = time
    )
}

fun ExchangeRateEntity.toDomainExchangeRate(): ExchangeRate {
    return ExchangeRate(
        assetIdBase = assetIdBase,
        assetIdQuote = assetIdQuote,
        rate = rate,
        time = time
    )
}

fun ExchangeRate.toLocalExchangeRate(): ExchangeRateEntity {
    return ExchangeRateEntity(
        assetIdBase = assetIdBase,
        assetIdQuote = assetIdQuote,
        rate = rate,
        time = time
    )
}