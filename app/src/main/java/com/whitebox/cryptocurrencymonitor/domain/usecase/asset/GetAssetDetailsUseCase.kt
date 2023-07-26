package com.whitebox.cryptocurrencymonitor.domain.usecase.asset

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAssetDetailsUseCase @Inject constructor(
    private val repository: CryptocurrencyRepository
) {
    suspend operator fun invoke(assetId: String): Flow<WorkResult<Asset?>> =
        repository.getAsset(assetId)
}