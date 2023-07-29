package com.whitebox.cryptocurrencymonitor.common

object Constants {

    const val BASE_URL = "https://rest.coinapi.io/v1/"
    const val EUR_ASSET_CODE = "EUR"

    enum class ImageSize(val size: String) {
        SMALL("32"),
        MEDIUM("64"),
        LARGE("128")
    }

    enum class YesOrNo(val value: Int) {
        YES(1),
        NO(0)
    }

}
