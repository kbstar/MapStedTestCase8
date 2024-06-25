package com.mapsted.mapstedtceight.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CitiesResponse(
    @SerializedName("data") var data: List<City> = ArrayList(),
) : Serializable