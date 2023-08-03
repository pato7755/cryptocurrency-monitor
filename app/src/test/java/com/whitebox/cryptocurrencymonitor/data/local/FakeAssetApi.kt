package com.whitebox.cryptocurrencymonitor.data.local

import com.whitebox.cryptocurrencymonitor.data.remote.AssetApi
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.AssetIconDto
import com.whitebox.cryptocurrencymonitor.data.remote.dto.ExchangeRateDto

class FakeAssetApi : AssetApi {
    override suspend fun getAssets(): List<AssetDto> {
        return listOf(
            AssetDto(
                assetId = "QQQ",
                name = "Bitcoin",
                typeIsCrypto = 1,
                dataEnd = "2021-02-18",
                dataOrderbookEnd = "2020-08-05",
                dataOrderbookStart = "2014-02-24",
                dataQuoteEnd = "2021-02-18",
                dataQuoteStart = "2014-04-01",
                dataStart = "2014-02-24",
                dataSymbolsCount = 44552,
                dataTradeEnd = "2020-08-05",
                dataTradeStart = "2010-07-17",
                idIcon = "4caf2b16-1b7a-4b8c-ad91-0b6f9e5a6124",
                priceUsd = 52123.0,
                volume1dayUsd = 6.590151,
                volume1hrsUsd = 0.2745905,
                volume1mthUsd = 0.0
            ),
            AssetDto(
                assetId = "USD",
                name = "US Dollar",
                typeIsCrypto = 0,
                dataEnd = "2021-02-18",
                dataOrderbookEnd = "2020-08-05",
                dataOrderbookStart = "2014-02-24",
                dataQuoteEnd = "2021-02-18",
                dataQuoteStart = "2014-04-01",
                dataStart = "2014-02-24",
                dataSymbolsCount = 44552,
                dataTradeEnd = "2020-08-05",
                dataTradeStart = "2010-07-17",
                idIcon = "4caf2b16-1b7a-4b8c-ad91-0b6f9e5a6124",
                priceUsd = 18.0,
                volume1dayUsd = 6.590151,
                volume1hrsUsd = 0.2745905,
                volume1mthUsd = 0.0
            ),
            AssetDto(
                assetId = "EUR",
                name = "Euro",
                typeIsCrypto = 0,
                dataEnd = "2021-02-18",
                dataOrderbookEnd = "2020-08-05",
                dataOrderbookStart = "2014-02-24",
                dataQuoteEnd = "2021-02-18",
                dataQuoteStart = "2014-04-01",
                dataStart = "2014-02-24",
                dataSymbolsCount = 44552,
                dataTradeEnd = "2020-08-05",
                dataTradeStart = "2010-07-17",
                idIcon = "4caf2b16-1b7a-4b8c-ad91-0b6f9e5a6124",
                priceUsd = 1.06,
                volume1dayUsd = 6.590151,
                volume1hrsUsd = 0.2745905,
                volume1mthUsd = 0.0
            ),
        )
    }

    override suspend fun getAssetDetails(assetId: String): List<AssetDto> {
        return listOf(
            AssetDto(
                assetId = "BTC",
                name = "Bitcoin",
                typeIsCrypto = 1,
                dataEnd = "2021-02-18",
                dataOrderbookEnd = "2020-08-05",
                dataOrderbookStart = "2014-02-24",
                dataQuoteEnd = "2021-02-18",
                dataQuoteStart = "2014-04-01",
                dataStart = "2014-02-24",
                dataSymbolsCount = 44552,
                dataTradeEnd = "2020-08-05",
                dataTradeStart = "2010-07-17",
                idIcon = "4caf2b16-1b7a-4b8c-ad91-0b6f9e5a6124",
                priceUsd = 52123.0,
                volume1dayUsd = 6.590151,
                volume1hrsUsd = 0.2745905,
                volume1mthUsd = 0.0
            )
        )
    }

    override suspend fun getAssetIcons(size: String): List<AssetIconDto> {
        return listOf(
            AssetIconDto(
                assetId = "BTC",
                url = "https://s3.eu-central-1.amazonaws.com/a-4b8c-ad91-0b6f9e5a6124.png"
            ),
            AssetIconDto(
                assetId = "USD",
                url = "https://s3.eu-central-1.amazonaws.com/bb8c-ad91-0b6f9e5a6124.png"
            ),
            AssetIconDto(
                assetId = "EUR",
                url = "https://s3.eu-central-1.amazonaws.com/bbb7a-4b8c-ad91-0b6f9e5a6124.png"
            )
        )
    }

    override suspend fun getExchangeRate(baseId: String): ExchangeRateDto {
        return ExchangeRateDto(
            assetIdBase = "BTC",
            assetIdQuote = "USD",
            rate = 52123.0,
            time = "2021-02-18T00:00:00.0000000Z"
        )
    }
}