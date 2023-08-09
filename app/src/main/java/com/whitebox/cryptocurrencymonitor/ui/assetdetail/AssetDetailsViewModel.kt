package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import android.util.Log
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
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssetDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAssetDetailsUseCase: GetAssetDetailsUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    val networkConnectivityService: NetworkConnectivityService
) : ViewModel() {

    private val _assetDetailsState = MutableStateFlow(AssetDetailsState())
    val assetDetailsState = _assetDetailsState.asStateFlow()

    private val _exchangeRateState = MutableStateFlow(ExchangeRateState())
    val exchangeRateState = _exchangeRateState.asStateFlow()

//    private val _networkConnectivityState: StateFlow<NetworkStatus> =
//        networkConnectivityService.networkStatus.stateIn(
//            initialValue = NetworkStatus.Unknown,
//            scope = viewModelScope,
//            started = WhileSubscribed(3000)
//        )

//    var networkConnectivityState = _networkConnectivityState

    init {
        Log.d("DetailsViewModel", "init")
        fetchData()
    }

    private fun fetchData() {
        Log.d("details fetchData:", "fetch")
        val assetId = savedStateHandle.get<String>("assetId") ?: ""

        viewModelScope.launch(Dispatchers.IO) {
            networkConnectivityService.networkStatus.distinctUntilChanged().collect { currentNetworkStatus ->

                if (currentNetworkStatus == NetworkStatus.Connected) {
                    Log.d("Details Network Status:", "Connected")
                getAssetDetails(assetId = assetId)
                getExchangeRate(assetId = assetId)
                } else {
                    Log.d("Details Network Status:", "Not Connected")
                    Log.d("Details Network Status:", currentNetworkStatus.toString())
                getAssetDetails(assetId = assetId, fetchFromRemote = false)
                getExchangeRate(assetId = assetId, fetchFromRemote = false)
                }
            }
        }
    }

    private fun getAssetDetails(assetId: String, fetchFromRemote: Boolean = true) {
        Log.d("getAssetDetails", "getAssetDetails")
        viewModelScope.launch(Dispatchers.IO) {
            getAssetDetailsUseCase.invoke(assetId = assetId, fetchFromRemote)
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
                            Log.d("getAssetDetails", "updateAssetDetailsState")
                            Log.d("getAssetDetails", result.data.toString())

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
        Log.d("getExchangeRate", "getExchangeRate")
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
                        Log.d("getExchangeRate", "updateExchangeRateState")
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
