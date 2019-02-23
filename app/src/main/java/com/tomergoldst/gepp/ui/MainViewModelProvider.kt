package com.tomergoldst.gepp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tomergoldst.gepp.data.GetGeoLocationService
import com.tomergoldst.gepp.data.GetNearestPlacesService

class MainViewModelProvider(private val application: Application,
                            private val getNearestPlacesService: GetNearestPlacesService,
                            private val getGeoLocationService: GetGeoLocationService) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, getNearestPlacesService, getGeoLocationService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}