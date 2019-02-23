package com.tomergoldst.gepp.data

import com.tomergoldst.gepp.model.ReverseGeoLocationResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetGeoLocationService {

    @GET("geocode/json")
    fun getReverseGeoCoding(@Query("latlng") latlng: String, @Query("key") key: String): Call<ReverseGeoLocationResult>

}