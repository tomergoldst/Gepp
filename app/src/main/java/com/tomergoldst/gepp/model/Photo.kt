package com.tomergoldst.gepp.model

import com.google.gson.annotations.SerializedName

class Photo(
    @SerializedName("photo_reference")
    val photoReference: String,

    val height: Int,

    val width: Int,

    @SerializedName("html_attributions")
    val htmlAttributions: Array<String>
)