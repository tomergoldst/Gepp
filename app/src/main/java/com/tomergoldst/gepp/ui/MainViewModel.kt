package com.tomergoldst.gepp.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.tomergoldst.gepp.R
import com.tomergoldst.gepp.data.Constants
import com.tomergoldst.gepp.data.GetGeoLocationService
import com.tomergoldst.gepp.data.GetNearestPlacesService
import com.tomergoldst.gepp.model.NearestPlacesResult
import com.tomergoldst.gepp.model.Place
import com.tomergoldst.gepp.model.ReverseGeoLocationResult
import com.tomergoldst.gepp.model.Status
import com.tomergoldst.gepp.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainViewModel(
    application: Application,
    private val getNearestPlacesService: GetNearestPlacesService,
    private val getGeoLocationService: GetGeoLocationService
) :
    AndroidViewModel(application) {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        const val NEAREST_PLACES_DEFAULT_RADIUS_METERS = 1500
    }

    private var mGeoKey =  getApplication<Application>().getString(R.string.geo_api_key)

    private var mCurrentLocationAddress: MutableLiveData<String> = MutableLiveData()
    private var mCurrentLocation: MutableLiveData<LatLng> = MutableLiveData()
    private var mPlaces: MutableLiveData<MutableList<Place>> = MutableLiveData()
    private var mStatus: MutableLiveData<Status> = MutableLiveData()

    fun init(){
        if (ContextCompat.checkSelfPermission(
                getApplication(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Got last known location. In some rare situations this can be null.
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication() as Context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val currentLocation = LatLng(location.latitude, location.longitude)
                        mCurrentLocation.postValue(currentLocation)
                        getLocationAddress(currentLocation)
                        getNearestPlaces(currentLocation, NEAREST_PLACES_DEFAULT_RADIUS_METERS)
                    } else {
                        mStatus.postValue(Status.NO_LOCATION_SERVICES)
                    }
                }
        } else {
            // todo show default location or show message
        }

        checkNetworkConnection()
    }

    fun getPlaces(): LiveData<MutableList<Place>> {
        return mPlaces
    }

    fun getCurrentLocationAddress(): LiveData<String> {
        return mCurrentLocationAddress
    }

    fun getCurrentLocation(): LiveData<LatLng>{
        return mCurrentLocation
    }

    fun getStatus(): LiveData<Status> {
        return mStatus
    }

    fun getNearestPlaces(location: LatLng, radius: Int) {
        // build query params
        val queryParams: MutableMap<String, String> = HashMap()
        queryParams[Constants.KEY] = mGeoKey
        queryParams[Constants.LOCATION] = "${location.latitude},${location.longitude}"
        queryParams[Constants.RADIUS] = radius.toString()
        queryParams[Constants.LANGUAGE] = Locale.getDefault().language

        // run get nearest places.json async
        getNearestPlacesService.nearestPlaces(queryParams).enqueue(object : Callback<NearestPlacesResult> {
            override fun onFailure(call: Call<NearestPlacesResult>, t: Throwable) {
                Log.e(TAG, "nearestPlaces:onFailure", t)
            }

            override fun onResponse(call: Call<NearestPlacesResult>, response: Response<NearestPlacesResult>) {
                Log.d(TAG, "nearestPlaces:onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    mPlaces.postValue(response.body()?.results?.toMutableList())
                } else {
                    Toast.makeText(
                        getApplication(),
                        "nearestPlaces response is unsuccessful",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }

    fun getLocationAddress(location: LatLng) {
        val loc = "${location.latitude},${location.longitude}"
        val language = Locale.getDefault().language

        getGeoLocationService.getReverseGeoCoding(loc, language, mGeoKey).enqueue(object : Callback<ReverseGeoLocationResult> {
            override fun onFailure(call: Call<ReverseGeoLocationResult>, t: Throwable) {
                Log.e(TAG, "getReverseGeoCoding:onFailure", t)
            }

            override fun onResponse(call: Call<ReverseGeoLocationResult>, response: Response<ReverseGeoLocationResult>) {
                Log.d(TAG, "getReverseGeoCoding:onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    mCurrentLocationAddress.postValue(response.body()?.results?.get(0)?.formattedAddress)
                } else {
                    Toast.makeText(
                        getApplication(),
                        "getReverseGeoCoding response is unsuccessful",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }

    private fun checkNetworkConnection() {
        if (!NetworkUtils.hasNetworkConnection(getApplication())) {
          mStatus.postValue(Status.NO_INTERNET_CONNECTION)
        }
    }

}