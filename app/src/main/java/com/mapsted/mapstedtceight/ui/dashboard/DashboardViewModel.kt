package com.mapsted.mapstedtceight.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapsted.mapstedtceight.data.BuildingsAnalyticalData
import com.mapsted.mapstedtceight.network.ApiCallState
import com.mapsted.mapstedtceight.network.NetworkAPI
import com.mapsted.mapstedtceight.network.ResponseState
import com.mapsted.mapstedtceight.network.models.AnalyticData
import com.mapsted.mapstedtceight.network.models.BuildingData
import com.mapsted.mapstedtceight.session.SessionPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val networkApi: NetworkAPI,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    lateinit var buildingsAnalyticalData: BuildingsAnalyticalData

    var selManufacturer: String? =null
    var selItemCategoryId: Long = 0L
    var selCountry: Map.Entry<String, MutableSet<String>>? = null
    var selState: String? = null
    var selItemId: Long = 0L

    var manufactures: MutableSet<String> = mutableSetOf()
    var categories: MutableSet<Long> = mutableSetOf()
    var countryStates: HashMap<String, MutableSet<String>> = HashMap()

    val stateBuildingsAnalyticalData = MutableStateFlow<ApiCallState<BuildingsAnalyticalData>>(ApiCallState.Idle())

    val stateBuildingData = MutableStateFlow<ApiCallState<List<BuildingData>>>(ApiCallState.Idle())
    val stateAnalyticsData = MutableStateFlow<ApiCallState<List<AnalyticData>>>(ApiCallState.Idle())

    fun getBuildingData() {
        viewModelScope.launch(Dispatchers.IO) {
            stateBuildingData.value = ApiCallState.Loading()
            networkApi.getBuildingData().let { responseState ->
                when (responseState) {
                    is ResponseState.Success -> {
                        stateBuildingData.value = ApiCallState.Success(responseState.response)
                    }

                    is ResponseState.Error -> {
                        stateBuildingData.value = ApiCallState.Error(responseState.error)
                    }
                }
            }
        }
    }

    fun getAnalyticData() {
        viewModelScope.launch(Dispatchers.IO) {
            stateBuildingData.value = ApiCallState.Loading()
            networkApi.getAnalyticData().let { responseState ->
                when (responseState) {
                    is ResponseState.Success -> {
                        stateAnalyticsData.value = ApiCallState.Success(responseState.response)
                    }

                    is ResponseState.Error -> {
                        stateAnalyticsData.value = ApiCallState.Error(responseState.error)
                    }
                }
            }
        }
    }

    fun getBuildingsAnalyticalData() {
        viewModelScope.launch(Dispatchers.IO) {
            stateBuildingsAnalyticalData.value = ApiCallState.Loading()
            networkApi.getBuildingData().let { responseStateBuildingData ->
                when (responseStateBuildingData) {
                    is ResponseState.Success -> {
                        val buildings = responseStateBuildingData.response

                        networkApi.getAnalyticData().let { responseStateAnalyticData ->
                            when (responseStateAnalyticData) {
                                is ResponseState.Success -> {
                                    val analytics = responseStateAnalyticData.response

                                    buildingsAnalyticalData = BuildingsAnalyticalData(
                                        buildings = buildings,
                                        analytics = analytics
                                    )

                                    prepareData()

                                    stateBuildingsAnalyticalData.value = ApiCallState.Success(
                                        buildingsAnalyticalData
                                    )
                                }

                                is ResponseState.Error -> {
                                    stateBuildingsAnalyticalData.value = ApiCallState.Error(responseStateAnalyticData.error)
                                }
                            }
                        }
                    }

                    is ResponseState.Error -> {
                        stateBuildingsAnalyticalData.value = ApiCallState.Error(responseStateBuildingData.error)
                    }
                }
            }
        }
    }

    private fun prepareData() {
        buildingsAnalyticalData.analytics.forEach { data ->
            manufactures.add(data.manufacturer)
            data.usageStatics?.sessionInfos?.forEach { info ->
                info.purchases.forEach { purchase ->
                    categories.add(purchase.itemCategoryId)
                }
            }
        }

        buildingsAnalyticalData.buildings.forEach { building ->
            countryStates[building.country] = mutableSetOf()
            countryStates[building.country]?.add(building.state)
        }
    }
}