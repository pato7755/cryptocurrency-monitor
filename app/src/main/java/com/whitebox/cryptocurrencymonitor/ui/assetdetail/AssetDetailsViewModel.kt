package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetDetailsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.exchangerate.GetExchangeRateUseCase
import com.whitebox.cryptocurrencymonitor.util.NetworkConnectivityService
import com.whitebox.cryptocurrencymonitor.util.NetworkStatus
import com.whitebox.cryptocurrencymonitor.util.Utilities.convertDate
import com.whitebox.cryptocurrencymonitor.util.Utilities.formatToCurrencyAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAssetDetailsUseCase: GetAssetDetailsUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    private val networkConnectivityService: NetworkConnectivityService
) : ViewModel() {

    private val _assetDetailsState = MutableStateFlow(AssetDetailsState())
    val assetDetailsState = _assetDetailsState.asStateFlow()

    private val _exchangeRateState = MutableStateFlow(ExchangeRateState())
    val exchangeRateState = _exchangeRateState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        val assetId = savedStateHandle.get<String>("assetId") ?: ""

        viewModelScope.launch(Dispatchers.IO) {
            // Observe network connectivity status
            networkConnectivityService.networkStatus.distinctUntilChanged().onStart {
                emit(NetworkStatus.Unknown)
            }.collect { currentNetworkStatus ->
                getAssetDetails(
                    assetId = assetId,
                    fetchFromRemote = (currentNetworkStatus == NetworkStatus.Connected)
                )
                getExchangeRate(
                    assetId = assetId,
                    fetchFromRemote = (currentNetworkStatus == NetworkStatus.Connected)
                )
            }
        }
    }

    private fun getAssetDetails(assetId: String, fetchFromRemote: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            getAssetDetailsUseCase.invoke(assetId = assetId, fetchFromRemote = fetchFromRemote)
                .collectLatest { result ->
                    when (result) {
                        is WorkResult.Loading -> {
                            withContext(Dispatchers.Main) {
                                _assetDetailsState.update { state ->
                                    state.copy(
                                        isLoading = true
                                    )
                                }
                            }
                        }

                        is WorkResult.Success -> {
                            withContext(Dispatchers.Main) {
                                _assetDetailsState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        asset = result.data
                                    )
                                }
                            }
                        }

                        is WorkResult.Error -> {
                            withContext(Dispatchers.Main) {
                                _assetDetailsState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        error = result.message
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun getExchangeRate(assetId: String, fetchFromRemote: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            getExchangeRateUseCase.invoke(
                baseAssetId = assetId,
                fetchFromRemote = fetchFromRemote
            ).collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        withContext(Dispatchers.Main) {
                            _exchangeRateState.update { state ->
                                state.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }

                    is WorkResult.Success -> {
                        withContext(Dispatchers.Main) {
                            _exchangeRateState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    exchangeRate = result.data
                                )
                            }
                        }
                    }

                    is WorkResult.Error -> {
                        withContext(Dispatchers.Main) {
                            _exchangeRateState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun formatAmount(amount: String): String {
        return amount.formatToCurrencyAmount()
    }

    fun convertDate(date: String): String {
        return date.convertDate()
    }

}
