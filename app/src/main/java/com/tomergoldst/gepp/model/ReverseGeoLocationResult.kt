package com.tomergoldst.gepp.model

import com.google.gson.annotations.SerializedName


data class ReverseGeoLocationResult(

    @SerializedName("plus_code")
    val plusCode: PlusCode,

    val results: List<AddressLookUpResponse>,

    val status: String

)