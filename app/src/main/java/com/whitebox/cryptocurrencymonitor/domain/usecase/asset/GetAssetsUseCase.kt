package com.whitebox.cryptocurrencymonitor.domain.usecase.asset

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAssetsUseCase @Inject constructor(
    private val repository: CryptocurrencyRepository
) {
    suspend operator fun invoke(fetchFromRemote: Boolean): Flow<WorkResult<List<Asset>>> =
        repository.getAssets(fetchFromRemote = fetchFromRemote)
}