package com.bendanfund.app.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FundDataParser {

    private val gson = Gson()

    fun parseSinaFundData(jsonpResponse: String): SinaFundData? {
        return try {
            val json = jsonpResponse
                .removePrefix("jsonpgz(")
                .removeSuffix(")")
            gson.fromJson(json, SinaFundData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun parseEastMoneyFundData(jsonResponse: String): List<EastMoneyFundData>? {
        return try {
            val type = object : TypeToken<List<EastMoneyFundData>>() {}.type
            gson.fromJson(jsonResponse, type)
        } catch (e: Exception) {
            null
        }
    }

    fun convertToEstimatedValue(sinaData: SinaFundData, shares: Double): EstimatedValueResponse {
        return EstimatedValueResponse(
            fundCode = sinaData.fundCode,
            estimatedValue = sinaData.estimatedValue * shares,
            estimatedChange = sinaData.estimatedValue * shares * sinaData.estimatedChangeRate / 100,
            estimatedChangeRate = sinaData.estimatedChangeRate,
            updateTime = sinaData.updateTime
        )
    }
}
