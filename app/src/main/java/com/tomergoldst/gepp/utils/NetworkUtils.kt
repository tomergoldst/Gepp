package com.tomergoldst.gepp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {

    fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

    fun hasNetworkConnection(context: Context): Boolean{
        val networkInfo = getActiveNetworkInfo(context)
        return networkInfo?.isConnected ?: false
    }
}