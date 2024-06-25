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

                                    stateBuildingsAnalyticalData.value = ApiCallState.Success(
                                        BuildingsAnalyticalData(
                                            buildings = buildings,
                                            analytics = analytics
                                        )
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
}