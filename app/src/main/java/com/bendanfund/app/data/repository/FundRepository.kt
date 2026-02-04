package com.bendanfund.app.data.repository

import com.bendanfund.app.data.remote.EstimatedValueResponse
import com.bendanfund.app.data.remote.FundApiService
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
                if (response.code == 0 && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBatchEstimates(fundCodes: List<String>): Result<List<EstimatedValueResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val codes = fundCodes.joinToString(",")
                val response = apiService.getBatchEstimate(codes)
                if (response.code == 0 && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun calculateEstimatedValue(fund: Fund, estimateData: EstimatedValueResponse): Fund {
        val estimatedValue = fund.shares * estimateData.estimatedValue
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
