package com.bendanfund.app.data.remote

import com.google.gson.annotations.SerializedName

data class FundDetailResponse(
    @SerializedName("fund_code")
    val fundCode: String,
    @SerializedName("fund_name")
    val fundName: String,
    @SerializedName("net_value")
    val netValue: Double,
    @SerializedName("estimated_value")
    val estimatedValue: Double,
    @SerializedName("acc_value")
    val accValue: Double,
    @SerializedName("daily_growth")
    val dailyGrowth: Double,
    @SerializedName("last_update")
    val lastUpdate: String
)

data class FundPositionResponse(
    @SerializedName("fund_code")
    val fundCode: String,
    @SerializedName("fund_name")
    val fundName: String,
    @SerializedName("hold_amount")
    val holdAmount: Double,
    @SerializedName("cost")
    val cost: Double,
    @SerializedName("shares")
    val shares: Double,
    @SerializedName("cost_per_share")
    val costPerShare: Double,
    @SerializedName("type")
    val type: String
)

data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T?
)

data class EstimatedValueResponse(
    @SerializedName("fund_code")
    val fundCode: String,
    @SerializedName("estimated_value")
    val estimatedValue: Double,
    @SerializedName("estimated_change")
    val estimatedChange: Double,
    @SerializedName("estimated_change_rate")
    val estimatedChangeRate: Double,
    @SerializedName("update_time")
    val updateTime: String
)
