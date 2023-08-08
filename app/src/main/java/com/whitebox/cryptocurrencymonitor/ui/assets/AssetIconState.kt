package com.whitebox.cryptocurrencymonitor.ui.assets

import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon

data class AssetIconState(
    val isLoading: Boolean = false,
    val assetIcons: List<AssetIcon?> = emptyList(),
    val isFetched: Boolean = false,
    val error: String? = null
)
