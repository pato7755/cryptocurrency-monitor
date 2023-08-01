package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetDetailsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.exchangerate.GetExchangeRateUseCase
import com.whitebox.cryptocurrencymonitor.util.NetworkConnectivityService
import com.whitebox.cryptocurrencymonitor.util.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAssetDetailsUseCase: GetAssetDetailsUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    networkConnectivityService: NetworkConnectivityService
) : ViewModel() {
    private val _assetDetailsState = MutableStateFlow(AssetDetailsState())
    val assetDetailsState = _assetDetailsState.asStateFlow()

    private val _exchangeRateState = MutableStateFlow(ExchangeRateState())
    val exchangeRateState = _exchangeRateState.asStateFlow()

    private val _networkConnectivityState: StateFlow<NetworkStatus> =
        networkConnectivityService.networkStatus.stateIn(
            initialValue = NetworkStatus.Unknown,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(3000)
        )

    init {
        val assetId = savedStateHandle.get<String>("assetId") ?: ""
        _networkConnectivityState.value.let { status ->
            if (status is NetworkStatus.Connected) {
                getAssetDetails(assetId = assetId)
                getExchangeRate(assetId = assetId)
            } else {
                getAssetDetails(assetId = assetId, fetchFromRemote = false)
                getExchangeRate(assetId = assetId, fetchFromRemote = false)
            }
        }
    }

    private fun getAssetDetails(assetId: String, fetchFromRemote: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            getAssetDetailsUseCase.invoke(assetId = assetId, fetchFromRemote)
                .collectLatest { result ->
                    when (result) {
                        is WorkResult.Loading -> {
                            _assetDetailsState.update { state ->
                                state.copy(
                                    isLoading = true,
                                    asset = result.data
                                )
                            }
                        }

                        is WorkResult.Success -> {
                            _assetDetailsState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    asset = result.data
                                )
                            }
                        }

                        is WorkResult.Error -> {
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

    private fun getExchangeRate(assetId: String, fetchFromRemote: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            getExchangeRateUseCase.invoke(
                baseAssetId = assetId,
                fetchFromRemote = fetchFromRemote
            ).collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        _exchangeRateState.update { state ->
                            state.copy(
                                isLoading = true,
                                exchangeRate = result.data
                            )
                        }
                    }

                    is WorkResult.Success -> {
                        _exchangeRateState.update { state ->
                            state.copy(
                                isLoading = false,
                                exchangeRate = result.data
                            )
                        }
                    }

                    is WorkResult.Error -> {
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
