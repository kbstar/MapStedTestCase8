package com.mapsted.mapstedtceight.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
{
    "manufacturer": "Motorola",
    "market_name": "Moto G4 Play",
    "codename": "harpia",
    "model": "Moto G Play",
    "usage_statistics": {
        "session_infos": [{
                "building_id": 17,
                "purchases": [{
                        "item_id": 9,
                        "item_category_id": 9,
                        "cost": 69.98
                    }
                ]
            }
        ]
    }
}
 */

data class AnalyticData(
    @SerializedName("manufacturer") var manufacturer: String = "",
    @SerializedName("market_name") var marketName: String = "",
    @SerializedName("codename") var codeName: String = "",
    @SerializedName("model") var model: String = "",
    @SerializedName("usage_statistics") var usageStatics: UsageStatics? = null,
) : Serializable {

    data class UsageStatics(
        @SerializedName("session_infos") var sessionInfos: List<SessionInfo> = emptyList(),
    ) : Serializable {

        data class SessionInfo(
            @SerializedName("building_id") var buildingId: Long = 0,
            @SerializedName("purchases") var purchases: List<Purchase> = emptyList(),
            var buildingData: BuildingData? = null,
        ) : Serializable {

            data class Purchase(
                @SerializedName("item_id") var itemId: Long = 0,
                @SerializedName("item_category_id") var itemCategoryId: Long = 0,
                @SerializedName("cost") var cost: Double = 0.0,
            ) : Serializable {

            }
        }
    }
}