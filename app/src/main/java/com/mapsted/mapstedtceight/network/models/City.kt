package com.mapsted.mapstedtceight.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
    val hasData: Boolean = true, //initial field
    @SerializedName("id") var id: Long = 0,
    @SerializedName("title_en") var titleEN: String = "",
) : Serializable