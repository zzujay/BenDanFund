package com.bendanfund.app.data.repository

import com.bendanfund.app.data.remote.EstimatedValueResponse
import com.bendanfund.app.data.remote.FundApiService
import com.bendanfund.app.data.remote.FundDataParser
import com.bendanfund.app.data.remote.RetrofitClient
import com.bendanfund.app.domain.model.Fund
import com.bendanfund.app.domain.model.FundType
import com.bendanfund.app.domain.model.UserPortfolio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FundRepository(
    private val apiService: FundApiService = RetrofitClient.fundApiService
) {

    suspend fun getFundEstimate(fundCode: String): Result<EstimatedValueResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFundEstimate(fundCode)
                val sinaData = FundDataParser.parseSinaFundData(response)

                if (sinaData != null) {
                    Result.success(
                        EstimatedValueResponse(
                            fundCode = sinaData.fundCode,
                            estimatedValue = sinaData.estimatedValue,
                            estimatedChange = sinaData.estimatedValue * sinaData.estimatedChangeRate / 100,
                            estimatedChangeRate = sinaData.estimatedChangeRate,
                            updateTime = sinaData.updateTime
                        )
                    )
                } else {
                    Result.failure(Exception("解析基金数据失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBatchEstimates(fundCodes: List<String>): Result<Map<String, EstimatedValueResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val results = mutableMapOf<String, EstimatedValueResponse>()

                fundCodes.forEach { code ->
                    val response = apiService.getFundEstimate(code)
                    val sinaData = FundDataParser.parseSinaFundData(response)

                    sinaData?.let {
                        results[code] = EstimatedValueResponse(
                            fundCode = it.fundCode,
                            estimatedValue = it.estimatedValue,
                            estimatedChange = it.estimatedValue * it.estimatedChangeRate / 100,
                            estimatedChangeRate = it.estimatedChangeRate,
                            updateTime = it.updateTime
                        )
                    }
                }

                if (results.isNotEmpty()) {
                    Result.success(results)
                } else {
                    Result.failure(Exception("获取基金数据失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun calculateEstimatedValue(fund: Fund, estimateData: EstimatedValueResponse): Fund {
        val shares = fund.holdingAmount / fund.costPerShare
        val estimatedValue = shares * estimateData.estimatedValue
        return fund.copy(
            estimatedValue = estimatedValue,
            netValue = estimateData.estimatedValue,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    fun calculatePortfolioEstimates(
        portfolio: UserPortfolio,
        estimates: Map<String, EstimatedValueResponse>
    ): UserPortfolio {
        val updatedFunds = portfolio.funds.map { fund ->
            val estimate = estimates[fund.code]
            if (estimate != null) {
                calculateEstimatedValue(fund, estimate)
            } else {
                fund
            }
        }

        val totalEstimatedValue = updatedFunds.sumOf { it.estimatedValue }
        val totalCost = updatedFunds.sumOf { it.cost }

        return portfolio.copy(
            funds = updatedFunds,
            totalCost = totalCost,
            totalEstimatedValue = totalEstimatedValue,
            lastUpdateTime = System.currentTimeMillis()
        )
    }
}

fun parseFundType(typeString: String): FundType {
    return when (typeString.uppercase()) {
        "STOCK" -> FundType.STOCK
        "BOND" -> FundType.BOND
        "MIXED" -> FundType.MIXED
        "INDEX" -> FundType.INDEX
        "QDII" -> FundType.QDII
        "CURRENCY" -> FundType.CURRENCY
        else -> FundType.STOCK
    }
}
