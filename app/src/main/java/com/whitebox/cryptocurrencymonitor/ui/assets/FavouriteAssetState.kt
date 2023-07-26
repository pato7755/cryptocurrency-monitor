package com.whitebox.cryptocurrencymonitor.ui.assets

import com.whitebox.cryptocurrencymonitor.domain.model.Asset

data class FavouriteAssetState(
    val isLoading: Boolean = false,
    val assets: List<Asset> = emptyList(),
    val error: String? = null
)
