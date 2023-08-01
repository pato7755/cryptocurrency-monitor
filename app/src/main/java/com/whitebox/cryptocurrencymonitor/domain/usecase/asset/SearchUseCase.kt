package com.whitebox.cryptocurrencymonitor.domain.usecase.asset

import com.whitebox.cryptocurrencymonitor.domain.repository.CryptocurrencyRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: CryptocurrencyRepository
) {
    suspend operator fun invoke(searchString: String) =
        repository.searchAssets(searchString)
}