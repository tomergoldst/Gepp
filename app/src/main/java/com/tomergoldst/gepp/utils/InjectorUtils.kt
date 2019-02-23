package com.tomergoldst.gepp.utils

import android.app.Application
import com.tomergoldst.gepp.data.GetGeoLocationService
import com.tomergoldst.gepp.data.GetNearestPlacesService
import com.tomergoldst.gepp.data.RetrofitClient
import com.tomergoldst.gepp.ui.KickOffViewModelProvider
import com.tomergoldst.gepp.ui.MainViewModelProvider
import retrofit2.Retrofit

// Inject classes needed for various Activities and Fragments.
object InjectorUtils {

    fun getMainViewModelProvider(application: Application): MainViewModelProvider{
        val retrofit = getRetrofitInstance()
        return MainViewModelProvider(application,
            retrofit.create(GetNearestPlacesService::class.java),
            retrofit.create(GetGeoLocationService::class.java))
    }

    fun getKickOffViewModelProvider(application: Application): KickOffViewModelProvider {
        return KickOffViewModelProvider(application)
    }

    fun getRetrofitInstance(): Retrofit {
        return RetrofitClient.retrofit!!
    }

}