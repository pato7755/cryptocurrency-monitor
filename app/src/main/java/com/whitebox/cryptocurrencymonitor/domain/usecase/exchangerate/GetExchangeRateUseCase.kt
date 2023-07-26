package com.whitebox.cryptocurrencymonitor.domain.usecase.exchangerate

import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.model.ExchangeRate
import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val repository: CryptocurrencyRepository
) {
    suspend operator fun invoke(baseAssetId: String): Flow<WorkResult<ExchangeRate>> =
        repository.getExchangeRate(baseAssetId)
}