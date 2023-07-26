package com.whitebox.cryptocurrencymonitor.domain.usecase.asset

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.AssetIcon
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAssetIconsUseCase @Inject constructor(
    private val repository: CryptocurrencyRepository
) {
    suspend operator fun invoke(size: String): Flow<WorkResult<List<AssetIcon?>>> =
        repository.getAssetIcons(size)
}