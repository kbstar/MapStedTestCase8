package com.mapsted.mapstedtceight.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    @SerializedName("token") var token: String = "",
) : Serializable