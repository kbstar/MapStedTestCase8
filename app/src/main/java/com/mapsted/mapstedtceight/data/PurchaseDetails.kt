package com.mapsted.mapstedtceight.data

import java.io.Serializable

data class PurchaseDetails(
    var totalByManufactures: HashMap<String, Double> = HashMap(),
    var totalByCategory: HashMap<Long, Double> = HashMap(),
    var totalByBuilding: HashMap<Long, Double> = HashMap(),
    var totalByCountry: HashMap<String, Double> = HashMap(),
    var totalByState: HashMap<String, Double> = HashMap(),
    var itemPurchaseCount: HashMap<Long, Int> = HashMap(),
    var buildingName: String = ""
) : Serializable
