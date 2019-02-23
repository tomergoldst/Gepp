package com.tomergoldst.gepp.model

import com.google.gson.annotations.SerializedName


data class Place(
    val geometry: Geometry,

    val icon: String,

    val id: String,

    val name: String,

    val photos: List<Photo>? = null,

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("plus_code")
    val plusCode: PlusCode,

    val rating: Float,

    val scope: String,

    val types: List<PlaceType>,

    @SerializedName("user_ratings_total")
    val userRatingTotal: String,

    val vicinity: String

)