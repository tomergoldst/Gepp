package com.tomergoldst.gepp.data

import com.tomergoldst.gepp.model.NearestPlacesResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface GetNearestPlacesService {

    @GET("place/nearbysearch/json")
    fun nearestPlaces(@QueryMap params: Map<String, String>): Call<NearestPlacesResult>

    @GET("place/nearbysearch/json")
    fun nextNearestPlaces(@Query("pagetoken") pageToken: String, @Query("key") key: String): Call<NearestPlacesResult>

}