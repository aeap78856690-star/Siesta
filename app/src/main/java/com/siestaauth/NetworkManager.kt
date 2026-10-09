package com.siestaauth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkManager(private val context: Context) {

    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getConnectionType(): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return "offline"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "offline"

        return when {
            capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
            else -> "offline"
        }
    }
}