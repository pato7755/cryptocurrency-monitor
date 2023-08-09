package com.whitebox.cryptocurrencymonitor.ui.assets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitebox.cryptocurrencymonitor.common.Constants
import com.whitebox.cryptocurrencymonitor.common.WorkResult
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.AddFavouriteAssetUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetIconsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetAssetsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.GetFavouriteAssetsUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.RemoveFavouriteAssetUseCase
import com.whitebox.cryptocurrencymonitor.domain.usecase.asset.SearchUseCase
import com.whitebox.cryptocurrencymonitor.ui.common.SearchBarState
import com.whitebox.cryptocurrencymonitor.util.NetworkConnectivityService
import com.whitebox.cryptocurrencymonitor.util.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
class AssetsViewModel @Inject constructor(
    private val getAssetsUseCase: GetAssetsUseCase,
    private val getAssetIconsUseCase: GetAssetIconsUseCase,
    private val getFavouriteAssetsUseCase: GetFavouriteAssetsUseCase,
    private val addFavouriteAssetUseCase: AddFavouriteAssetUseCase,
    private val removeFavouriteAssetUseCase: RemoveFavouriteAssetUseCase,
    private val searchUseCase: SearchUseCase,
    val networkConnectivityService: NetworkConnectivityService
) : ViewModel() {
    private val _assetState = MutableStateFlow(AssetState())
    val assetState = _assetState.asStateFlow()

    private val _iconState = MutableStateFlow(AssetIconState())

    private val _searchBarState = MutableStateFlow(SearchBarState())
    val searchBarState = _searchBarState.asStateFlow()

    var searchJob: Job? = null

    private val _networkConnectivityState: StateFlow<NetworkStatus> =
        networkConnectivityService.networkStatus.stateIn(
            initialValue = NetworkStatus.Unknown,
            scope = viewModelScope,
            started = WhileSubscribed(3000)
        )

    var networkConnectivityState = _networkConnectivityState

    init {
        fetchData()
    }
    fun fetchDatae() {
        Log.d("Asset fetchData:", "fetch")
        if (_networkConnectivityState.value == NetworkStatus.Connected) {
            Log.d("Network Status:", "Connected")
            getAssetsAndIcons()
        } else if (_networkConnectivityState.value == NetworkStatus.Disconnected) {
            Log.d("Network Status:", "Not Connected")
            getAssetsAndIcons(fetchFromRemote = false)
        }
    }

    fun fetchData() {
        Log.d("Asset fetchData:", "fetch")
        viewModelScope.launch(Dispatchers.IO) {
            networkConnectivityService.networkStatus.distinctUntilChanged()
                .collect { currentNetworkStatus ->

                    if (currentNetworkStatus == NetworkStatus.Connected) {
                        Log.d("Network Status:", "Connected")
                        getAssetsAndIcons()
                    } else if (currentNetworkStatus == NetworkStatus.Disconnected) {
                        Log.d("Network Status:", "Not Connected")
                        getAssetsAndIcons(fetchFromRemote = false)
                    }
                }
        }
    }

    fun getAssetsAndIcons(fetchFromRemote: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            val assetsResult = async {
                Log.d("call getAssetsUseCase:", "getAssetsUseCase")
                Log.d("fetchFromRemote:", fetchFromRemote.toString())
                getAssetsUseCase.invoke(fetchFromRemote)
            }

            assetsResult.await().collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        withContext(Dispatchers.Main) {
                            _assetState.update { state ->
                                state.copy(
                                    isLoading = true,
//                                assets = result.data ?: emptyList()
                                )
                            }
                        }
                    }

                    is WorkResult.Success -> {
                        Log.d("Dao Result:", result.data.toString())
                        withContext(Dispatchers.Main) {
                            _assetState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    assets = result.data ?: emptyList()
                                )
                            }
                        }
                        if (!_iconState.value.isFetched) {
                            // Get asset icons
                            getAssetIcons()
                        } else {
                            updateAssetsWithIcons()
                        }
                    }

                    is WorkResult.Error -> {
                        withContext(Dispatchers.Main) {
                            _assetState.update { state ->
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

    private fun getAssetIcons() {
        Log.d("getAssetIcons", "getAssetIcons")
        viewModelScope.launch(Dispatchers.IO) {
            val iconsResult = async {
                Log.d("call getAssetIcons:", "getAssetIconsUseCase")
                getAssetIconsUseCase.invoke(Constants.ImageSize.MEDIUM.size)
            }
            iconsResult.await().collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        withContext(Dispatchers.Main) {
                            _iconState.update { state ->
                                state.copy(isLoading = true)
                            }
                        }
                    }

                    is WorkResult.Success -> {
                        withContext(Dispatchers.Main) {
                            Log.d("getAssetIcons", "update icons state")
                            _iconState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    assetIcons = result.data ?: emptyList(),
                                    isFetched = true
                                )
                            }
                        }
                        // Update assets with icons
                        updateAssetsWithIcons()

                    }

                    is WorkResult.Error -> {
                        withContext(Dispatchers.Main) {
                            _iconState.update { state ->
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

    private fun updateAssetsWithIcons() {
        Log.d("updateAssetsWithIcons", "updateAssetsWithIcons")
        _assetState.update { state ->
            state.copy(
                assets = state.assets.map { asset ->
                    asset.copy(
                        iconUrl = _iconState.value.assetIcons.find { icon ->
                            icon?.assetId == asset.assetId
                        }?.url
                    )
                }
            )
        }
    }

    fun onSearchTextChanged(text: String) {
        _searchBarState.update { state ->
            state.copy(
                searchString = text,
            )
        }
        if (text.isNotEmpty()) {
            searchJob = viewModelScope.launch(Dispatchers.IO) { delayThenFetchAssets() }
        } else {
            searchJob?.cancel()
        }
    }

    private suspend fun delayThenFetchAssets() {
        _assetState.update { state ->
            state.copy(
                isLoading = true
            )
        }
        delay(3000L)

        searchUseCase.invoke(_searchBarState.value.searchString).collectLatest { result ->
            when (result) {
                is WorkResult.Loading -> {
                    _assetState.update { state ->
                        state.copy(
                            isLoading = true
                        )
                    }
                }

                is WorkResult.Success -> {
                    _assetState.update { state ->
                        state.copy(
                            isLoading = false,
                            assets = result.data ?: emptyList()
                        )
                    }
                    if (!_iconState.value.isFetched) {
                        // Get asset icons
                        getAssetIcons()
                    } else {
                        updateAssetsWithIcons()
                    }
                }

                is WorkResult.Error -> {
                    withContext(Dispatchers.Main) {
                        _assetState.update { state ->
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

    fun getFavouriteAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            getFavouriteAssetsUseCase.invoke().collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        _assetState.update { state ->
                            state.copy(isFavourite = true)
                        }
                    }

                    is WorkResult.Success -> {
                        Log.d("Favourite Assets:", result.data.toString())
                        _assetState.update { state ->
                            state.copy(
                                isFavourite = false,
                                assets = result.data ?: emptyList()
                            )
                        }
                        Log.d("Favourite Assets:", _assetState.value.assets.toString())
                        if (!_iconState.value.isFetched) {
                            // Get asset icons
                            getAssetIcons()
                        } else {
                            updateAssetsWithIcons()
                        }
                    }

                    is WorkResult.Error -> {
                        withContext(Dispatchers.Main) {
                            _assetState.update { state ->
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

    fun addFavouriteAsset(assetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (addFavouriteAssetUseCase.invoke(assetId = assetId)) {
                _assetState.update { state ->
                    state.copy(
                        isFavourite = true
                    )
                }
            }
        }
    }

    fun removeFavouriteAsset(assetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (removeFavouriteAssetUseCase.invoke(assetId = assetId)) {
                _assetState.update { state ->
                    state.copy(
                        isFavourite = false
                    )
                }
            }
        }
    }

}