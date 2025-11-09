package com.rawderm.taaza.today.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED

fun Context.checkInternet(callback: (Boolean) -> Unit) {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork
    val capabilities = cm.getNetworkCapabilities(network)

    return callback(
        capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
                && capabilities.hasCapability(NET_CAPABILITY_VALIDATED)
    )
}