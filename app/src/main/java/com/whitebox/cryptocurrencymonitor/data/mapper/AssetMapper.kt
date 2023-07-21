package com.whitebox.cryptocurrencymonitor.data.mapper

import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetIconDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.ExchangeRateDto
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate

fun AssetDto.toDomainAsset(): Asset {
    return Asset(
        assetId = assetId,
        idIcon = idIcon,
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