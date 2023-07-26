package com.whitebox.cryptocurrencymonitor.ui.assets

import com.whitebox.cryptocurrencymonitor.domain.model.Asset

data class AssetState(
    val isLoading: Boolean = false,
    val assets: List<Asset> = emptyList(),
    val error: String? = ""
)
