package com.tomergoldst.gepp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tomergoldst.gepp.data.GetGeoLocationService

class KickOffViewModelProvider(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KickOffViewModel::class.java)) {
            return KickOffViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}