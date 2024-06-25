package com.mapsted.mapstedtceight.data

import com.mapsted.mapstedtceight.network.models.AnalyticData
import com.mapsted.mapstedtceight.network.models.BuildingData
import java.io.Serializable

data class BuildingsAnalyticalData(
    val buildings: List<BuildingData>, val analytics: List<AnalyticData>
) : Serializable {
    
}