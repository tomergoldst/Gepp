package com.tomergoldst.gepp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomergoldst.gepp.R
import com.tomergoldst.gepp.model.Status
import com.tomergoldst.gepp.utils.NetworkUtils

class KickOffViewModel(
    application: Application
) :
    AndroidViewModel(application) {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var mStatus: MutableLiveData<Status> = MutableLiveData()

    fun start(){
        if (hasNetworkConnection()) {
            if (hasGeoApiKey()){
                mStatus.postValue(Status.START)
            }
        }
    }

    fun getStatus(): LiveData<Status> {
        return mStatus
    }

    private fun hasNetworkConnection(): Boolean {
        if (!NetworkUtils.hasNetworkConnection(getApplication())) {
            mStatus.postValue(Status.NO_INTERNET_CONNECTION)
            return false
        }

        return true
    }

    private fun hasGeoApiKey() : Boolean
    {
        val geoApiKey = getApplication<Application>().getString(R.string.geo_api_key)
        if (geoApiKey == "YOUR_GEO_API_KEY_HERE") {
            mStatus.postValue(Status.NO_GEO_API_KEY)
            return false
        }

        return true
    }

}