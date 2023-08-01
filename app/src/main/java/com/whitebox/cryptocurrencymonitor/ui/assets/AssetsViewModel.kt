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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val getAssetsUseCase: GetAssetsUseCase,
    private val getAssetIconsUseCase: GetAssetIconsUseCase,
    private val getFavouriteAssetsUseCase: GetFavouriteAssetsUseCase,
    private val addFavouriteAssetUseCase: AddFavouriteAssetUseCase,
    private val removeFavouriteAssetUseCase: RemoveFavouriteAssetUseCase,
    private val searchUseCase: SearchUseCase,
    networkConnectivityService: NetworkConnectivityService
) : ViewModel() {
    private val _assetState = MutableStateFlow(AssetState())
    val assetState = _assetState.asStateFlow()

    private val _iconState = MutableStateFlow(AssetIconState())

    private val _favouriteAssetState = MutableStateFlow(FavouriteAssetState())
    val favouriteAssetState = _favouriteAssetState.asStateFlow()

    private val _searchBarState = MutableStateFlow(SearchBarState())
    val searchBarState = _searchBarState.asStateFlow()

    var searchJob: Job? = null

    private val _networkConnectivityState: StateFlow<NetworkStatus> =
        networkConnectivityService.networkStatus.stateIn(
            initialValue = NetworkStatus.Unknown,
            scope = viewModelScope,
            started = WhileSubscribed(3000)
        )
    val networkConnectivityState = _networkConnectivityState

    init {
        _networkConnectivityState.value.let { status ->
            if (status is NetworkStatus.Connected) {
                getAssetsAndIcons()
            } else {
                getAssetsAndIcons(fetchFromRemote = false)
            }
        }
    }

    private fun getAssetsAndIcons(fetchFromRemote: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            val assetsResult = async {
                getAssetsUseCase.invoke(fetchFromRemote)
            }

            assetsResult.await().collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        _assetState.update { state ->
                            state.copy(
                                isLoading = true,
                                assets = result.data ?: emptyList()
                            )
                        }
                    }

                    is WorkResult.Success -> {
                        Log.d("Updated Dao Result:", result.data.toString())
                        _assetState.update { state ->
                            state.copy(
                                isLoading = false,
                                assets = result.data ?: emptyList()
                            )
                        }
                        // Get asset icons
                        getAssetIcons()
                    }

                    is WorkResult.Error -> {
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

    private fun getAssetIcons() {
        viewModelScope.launch(Dispatchers.IO) {
            val iconsResult = async {
                getAssetIconsUseCase.invoke(Constants.ImageSize.MEDIUM.size)
            }
            iconsResult.await().collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        _iconState.update { state ->
                            state.copy(isLoading = true)
                        }
                    }

                    is WorkResult.Success -> {
                        _iconState.update { state ->
                            state.copy(
                                isLoading = false,
                                assetIcons = result.data ?: emptyList()
                            )
                        }
                        // Update assets with icons
                        updateAssetsWithIcons()
                    }

                    is WorkResult.Error -> {
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

    private fun updateAssetsWithIcons() {
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
                    // Get asset icons
                    getAssetIcons()
                }

                is WorkResult.Error -> {
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

    fun getFavouriteAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            getFavouriteAssetsUseCase.invoke().collectLatest { result ->
                when (result) {
                    is WorkResult.Loading -> {
                        _favouriteAssetState.update { state ->
                            state.copy(isFavourite = true)
                        }
                    }

                    is WorkResult.Success -> {
                        _favouriteAssetState.update { state ->
                            state.copy(
                                isFavourite = false,
//                                assets = result.data ?: emptyList()
                            )
                        }
                    }

                    is WorkResult.Error -> {
                        _favouriteAssetState.update { state ->
                            state.copy(
//                                isLoading = false,
//                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateFavouriteAssetState(assetId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _favouriteAssetState.update { state ->
//                state.copy(
//                    isFavourite = getFavouriteAssetsUseCase.invoke(assetId = assetId)
//                )
//            }
//        }
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