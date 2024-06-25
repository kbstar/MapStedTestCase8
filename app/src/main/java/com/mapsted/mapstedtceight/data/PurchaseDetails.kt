package com.mapsted.mapstedtceight.data

import com.mapsted.mapstedtceight.network.models.BuildingData
import java.io.Serializable

data class PurchaseDetails(
    var totalByManufactures: HashMap<String, Double> = HashMap(),
    var totalByCategory: HashMap<Long, Double> = HashMap(),
    var totalByBuilding: LinkedHashMap<BuildingData, Double> = LinkedHashMap(),
    var totalByCountry: HashMap<String, Double> = HashMap(),
    var totalByState: HashMap<String, Double> = HashMap(),
    var itemPurchaseCount: HashMap<Long, Int> = HashMap(),
    var buildingNameWithHighPurchase: String = ""
) : Serializable
