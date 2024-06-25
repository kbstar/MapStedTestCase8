package com.mapsted.mapstedtceight.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
{
    "building_id": 1,
    "building_name": "Centre Eaton - Montreal",
    "city": "Montreal",
    "state": "Quebec",
    "country": "Canada"
}
 */

data class BuildingData(
    @SerializedName("building_id") var id: Long = 0,
    @SerializedName("building_name") var name: String = "",
    @SerializedName("city") var city: String = "",
    @SerializedName("state") var state: String = "",
    @SerializedName("country") var country: String = "",
) : Serializable