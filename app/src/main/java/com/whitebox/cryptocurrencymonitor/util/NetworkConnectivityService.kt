package com.whitebox.cryptocurrencymonitor.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

interface NetworkConnectivityService {
    val networkStatus: Flow<NetworkStatus>
    fun isNetworkAvailable(): Boolean
}

/**
 * Implementation of the NetworkConnectivityService interface providing network status information.
 *
 * This class monitors the network connectivity status and provides a [Flow] of [NetworkStatus]
 * representing whether the device is currently connected to a network or disconnected.
 */
class NetworkConnectivityServiceImpl @Inject constructor(
    context: Context
) : NetworkConnectivityService {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * A [Flow] of [NetworkStatus] that emits the current network connectivity status.
     *
     * The [Flow] emits [NetworkStatus.Connected] when the device becomes connected to a network,
     * and [NetworkStatus.Disconnected] when the device becomes disconnected from the network.
     *
     * The emitted values are distinct until a change in connectivity status occurs.
     */
    override val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.d("onAvailable: $network")
                trySend(NetworkStatus.Connected)
            }

            override fun onUnavailable() {
                Timber.d("onUnavailable")
                trySend(NetworkStatus.Disconnected)
            }

            override fun onLost(network: Network) {
                Timber.d("onUnavailable")
                trySend(NetworkStatus.Disconnected)
            }

        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(request, connectivityCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
        }
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun isNetworkAvailable(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}
