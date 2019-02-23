package com.tomergoldst.gepp.model

import com.google.gson.annotations.SerializedName


data class AddressLookUpResponse(

    @SerializedName("formatted_address")
    val formattedAddress: String

)