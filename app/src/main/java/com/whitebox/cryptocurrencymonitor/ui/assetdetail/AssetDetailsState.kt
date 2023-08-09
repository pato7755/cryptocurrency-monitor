package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import com.whitebox.cryptocurrencymonitor.domain.model.Asset

data class AssetDetailsState(
    val isLoading: Boolean = false,
    val asset: Asset? = null,
    val isFavourite: Boolean = false,
    val error: String? = null
)
