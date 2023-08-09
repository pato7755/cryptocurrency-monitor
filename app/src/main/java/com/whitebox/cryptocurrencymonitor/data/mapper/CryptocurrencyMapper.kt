package com.whitebox.cryptocurrencymonitor.data.mapper

import com.whitebox.cryptocurrencymonitor.data.local.entity.AssetEntity
import com.whitebox.cryptocurrencymonitor.data.local.entity.ExchangeRateEntity
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetIconDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.ExchangeRateDto
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate

// Asset DTO to Domain Model mapper
fun AssetDto.toDomainAsset(): Asset {
    return Asset(
        assetId = assetId,
        name = name,
        typeIsCrypto = typeIsCrypto,
        priceUsd = priceUsd.toString()
    )
}

// Asset Entity to Domain Model mapper
fun AssetEntity.toDomainAsset(): Asset {
    return Asset(
        assetId = assetId,
        name = name,
        typeIsCrypto = typeIsCrypto,
        isFavourite = isFavourite,
        priceUsd = priceUsd
    )
}

// Asset Domain Model to Entity mapper
fun Asset.toLocalAsset(): AssetEntity {
    return AssetEntity(
        assetId = assetId,
        name = name,
        typeIsCrypto = typeIsCrypto,
        isFavourite = isFavourite,
        priceUsd = priceUsd
    )
}

// AssetIcon DTO to Domain Model mapper
fun AssetIconDto.toDomainAssetIcon(): AssetIcon {
    return AssetIcon(
        assetId = assetId,
        url = url
    )
}

// ExchangeRate DTO to Domain Model mapper
fun ExchangeRateDto.toDomainExchangeRate(): ExchangeRate {
    return ExchangeRate(
        assetIdBase = assetIdBase,
        assetIdQuote = assetIdQuote,
        rate = rate,
        time = time
    )
}

// ExchangeRate Entity to Domain Model mapper
fun ExchangeRateEntity.toDomainExchangeRate(): ExchangeRate {
    return ExchangeRate(
        assetIdBase = assetIdBase,
        assetIdQuote = assetIdQuote,
        rate = rate,
        time = time
    )
}

// ExchangeRate Domain Model to Entity mapper
fun ExchangeRate.toLocalExchangeRate(): ExchangeRateEntity {
    return ExchangeRateEntity(
        assetIdBase = assetIdBase,
        assetIdQuote = assetIdQuote,
        rate = rate,
        time = time
    )
}