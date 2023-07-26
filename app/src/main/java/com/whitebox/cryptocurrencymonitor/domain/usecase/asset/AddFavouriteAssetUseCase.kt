package com.whitebox.cryptocurrencymonitor.domain.usecase.asset

import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import javax.inject.Inject

class AddFavouriteAssetUseCase @Inject constructor(
    private val repository: CryptocurrencyRepository
) {
    suspend operator fun invoke(assetId: String) = repository.addFavouriteAsset(assetId)
}