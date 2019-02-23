package com.tomergoldst.gepp.model

import com.google.gson.annotations.SerializedName


data class NearestPlacesResult(

    @SerializedName("html_attributions")
    val htmlAttributions : List<String>,

    @SerializedName("next_page_token")
    val nextPageToken: String,

    val results: List<Place>,

    val status: String

)