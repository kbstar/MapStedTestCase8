package com.mapsted.mapstedtceight.network

import com.mapsted.mapstedtceight.network.models.AnalyticData
import com.mapsted.mapstedtceight.network.models.BuildingData
import retrofit2.Response
import retrofit2.http.*

interface NetworkApiService {
    @GET("GetBuildingData")
    suspend fun getBuildingData(): Response<List<BuildingData>>

    @GET("GetAnalyticData")
    suspend fun getAnalyticData(): Response<List<AnalyticData>>
}