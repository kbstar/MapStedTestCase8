package com.mapsted.mapstedtceight.network

import com.mapsted.mapstedtceight.network.models.CitiesResponse
import com.mapsted.mapstedtceight.network.models.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface NetworkApiService {

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("uid") uid: String,
    ): Response<LoginResponse>

    @GET("api/getCities")
    suspend fun getCities(
        @Header("Authorization") token: String,
    ): Response<CitiesResponse>
}