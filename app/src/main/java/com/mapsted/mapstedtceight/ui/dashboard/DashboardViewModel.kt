package com.mapsted.mapstedtceight.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapsted.mapstedtceight.data.BuildingsAnalyticalData
import com.mapsted.mapstedtceight.data.PurchaseDetails
import com.mapsted.mapstedtceight.network.ApiCallState
import com.mapsted.mapstedtceight.network.NetworkAPI
import com.mapsted.mapstedtceight.network.ResponseState
import com.mapsted.mapstedtceight.network.models.AnalyticData
import com.mapsted.mapstedtceight.network.models.BuildingData
import com.mapsted.mapstedtceight.session.SessionPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val networkApi: NetworkAPI,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    //holding all data of original response
    lateinit var buildingsAnalyticalData: BuildingsAnalyticalData

    //selection values stored
    var selManufacturer: String? = null
    var selCategory: Map.Entry<Long, MutableSet<Long>>? = null
    var selCountry: Map.Entry<String, MutableSet<String>>? = null
    var selState: String? = null
    var selItemId: Long = 0L

    //all filtered data from response
    var manufactures: MutableSet<String> = mutableSetOf()
    var categories: HashMap<Long, MutableSet<Long>> = HashMap()
    var countryStates: HashMap<String, MutableSet<String>> = HashMap()

    //state of calculation changes
    val statePurchaseDetails = MutableSharedFlow<PurchaseDetails>()

    //data holding state of ViewModel and API calls
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
                    if (!categories.containsKey(purchase.itemCategoryId)) {
                        categories[purchase.itemCategoryId] = mutableSetOf()
                    }
                    categories[purchase.itemCategoryId]?.add(purchase.itemId)
                }
            }
        }

        buildingsAnalyticalData.buildings.forEach { building ->
            if (!countryStates.containsKey(building.country)) {
                countryStates[building.country] = mutableSetOf()
            }
            countryStates[building.country]?.add(building.state)
        }
    }

    fun calculateTotalPurchases() {
        val result = PurchaseDetails()
        viewModelScope.launch(Dispatchers.IO) {
            buildingsAnalyticalData.analytics.forEach { data ->
                if (!result.totalByManufactures.keys.contains(data.manufacturer)) {
                    result.totalByManufactures[data.manufacturer] = 0.0
                }

                data.usageStatics?.sessionInfos?.forEach { info ->
                    if (!result.totalByBuilding.keys.contains(info.buildingId)) {
                        result.totalByBuilding[info.buildingId] = 0.0
                    }

                    info.purchases.forEach { purchase ->
                        if (!result.totalByCategory.keys.contains(purchase.itemCategoryId)) {
                            result.totalByCategory[purchase.itemCategoryId] = 0.0
                        }
                        if (!result.itemPurchaseCount.keys.contains(purchase.itemId)) {
                            result.itemPurchaseCount[purchase.itemId] = 0
                        }

                        selManufacturer?.let { manufactur ->
                            selCategory?.key?.let { category ->
                                if (manufactur == data.manufacturer) {
                                    result.totalByManufactures[data.manufacturer] = (result.totalByManufactures[data.manufacturer] ?: 0.0) + purchase.cost
                                    result.totalByBuilding[info.buildingId] = (result.totalByBuilding[info.buildingId] ?: 0.0) + purchase.cost

                                    if (purchase.itemCategoryId == category) {
                                        result.totalByCategory[purchase.itemCategoryId] = (result.totalByCategory[purchase.itemCategoryId] ?: 0.0) + purchase.cost
                                        result.itemPurchaseCount[purchase.itemId] = (result.itemPurchaseCount[purchase.itemId] ?: 1) + 1
                                    }
                                }
                            }
                        }
                    }
                }
            }

            result.totalByBuilding.maxBy { it.value }.key.let { buildingId ->
                buildingsAnalyticalData.buildings.find { it.id == buildingId }?.let { buildingData ->
                    result.buildingNameWithHighPurchase = buildingData.name
                }
            }

            statePurchaseDetails.emit(result)
        }
    }
}