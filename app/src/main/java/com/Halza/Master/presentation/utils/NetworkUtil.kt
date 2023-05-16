package com.Halza.Master.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService

class NetworkUtil {
    companion object {
        fun registerNetworkChange(
            context: Context,
            onNetworkUpdated: (isAvailable: Boolean, networkType: NetworkType) -> Unit
        ): Unit {
            val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
            val debounce = Debounce(2000)

            connectivityManager?.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    debounce.doAction { onNetworkUpdated(false, NetworkType.NA) }
                }

                override fun onCapabilitiesChanged(
                    network: Network, networkCapabilities: NetworkCapabilities
                ) {
                    val networkType = detectNetworkCapability(networkCapabilities)
                    debounce.doAction {
                        onNetworkUpdated(
                            networkType != NetworkType.NA,
                            networkType
                        )
                    }
                }
            })
        }

        fun checkNetworkConnection(context: Context): Boolean {
            val connectivityManager = getSystemService(context, ConnectivityManager::class.java)

            val capabilities =
                connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return detectNetworkCapability(capabilities) != NetworkType.NA
            }

            return false
        }

        private fun detectNetworkCapability(networkCapabilities: NetworkCapabilities): NetworkType {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            ) {
                return NetworkType.Wifi
            }

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkType.Cellular
            }

            return NetworkType.NA
        }
    }
}