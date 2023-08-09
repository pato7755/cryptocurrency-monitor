package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate

data class ExchangeRateState(
    val isLoading: Boolean = false,
    val exchangeRate: ExchangeRate? = null,
    val error: String? = null
)
