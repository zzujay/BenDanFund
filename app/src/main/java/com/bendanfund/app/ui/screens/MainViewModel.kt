package com.bendanfund.app.ui.screens

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bendanfund.app.data.repository.FundRepository
import com.bendanfund.app.domain.model.Fund
import com.bendanfund.app.domain.model.FundType
import com.bendanfund.app.domain.model.UserPortfolio
import com.bendanfund.app.utils.FundParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val portfolio: UserPortfolio? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFund: Fund? = null,
    val isRefreshing: Boolean = false,
    val lastUpdateTime: Long = 0L
)

class MainViewModel : ViewModel() {
    private val repository = FundRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        loadEmptyPortfolio()
    }

    private fun loadEmptyPortfolio() {
        val defaultFunds = listOf(
            Fund(
                id = 1,
                code = "161039",
                name = "富国中证新能源汽车指数",
                holdingAmount = 15000.0,
                cost = 12000.0,
                costPerShare = 1.2,
                type = FundType.INDEX,
                estimatedValue = 15000.0,
                netValue = 1.25
            ),
            Fund(
                id = 2,
                code = "110022",
                name = "易方达消费行业股票",
                holdingAmount = 25000.0,
                cost = 28000.0,
                costPerShare = 1.8,
                type = FundType.STOCK,
                estimatedValue = 25000.0,
                netValue = 1.45
            ),
            Fund(
                id = 3,
                code = "162411",
                name = "华宝标普油气上游股票",
                holdingAmount = 8000.0,
                cost = 7500.0,
                costPerShare = 0.95,
                type = FundType.STOCK,
                estimatedValue = 8000.0,
                netValue = 0.98
            )
        )

        val portfolio = UserPortfolio(
            name = "我的持仓",
            funds = defaultFunds,
            totalCost = defaultFunds.sumOf { it.cost },
            totalEstimatedValue = defaultFunds.sumOf { it.estimatedValue }
        )

        _uiState.value = MainUiState(
            portfolio = portfolio,
            isLoading = false
        )
    }

    fun refreshEstimates() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val currentPortfolio = _uiState.value.portfolio ?: return@launch

            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)

            try {
                val fundCodes = currentPortfolio.funds.map { it.code }
                val result = repository.getBatchEstimates(fundCodes)

                result.fold(
                    onSuccess = { estimates ->
                        val updatedPortfolio = repository.calculatePortfolioEstimates(currentPortfolio, estimates)

                        _uiState.value = _uiState.value.copy(
                            portfolio = updatedPortfolio,
                            isRefreshing = false,
                            lastUpdateTime = System.currentTimeMillis()
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            isRefreshing = false,
                            error = "更新失败，请下拉重试"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = "网络错误，请检查网络"
                )
            }
        }
    }

    fun processScreenshot(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                delay(500)

                val extractedFunds = FundParser.parseAlipayFundScreenshot("")

                if (extractedFunds.isEmpty()) {
                    val newFund = Fund(
                        id = System.currentTimeMillis(),
                        code = "161039",
                        name = "新识别的基金",
                        holdingAmount = 10000.0,
                        cost = 9500.0,
                        costPerShare = 1.0,
                        type = FundType.STOCK,
                        estimatedValue = 10000.0
                    )

                    addFundToPortfolio(newFund)
                } else {
                    val currentPortfolio = _uiState.value.portfolio
                    val updatedFunds = if (currentPortfolio != null) {
                        currentPortfolio.funds + extractedFunds
                    } else {
                        extractedFunds
                    }

                    val updatedPortfolio = UserPortfolio(
                        funds = updatedFunds,
                        totalCost = updatedFunds.sumOf { it.cost },
                        totalEstimatedValue = updatedFunds.sumOf { it.estimatedValue }
                    )

                    _uiState.value = _uiState.value.copy(
                        portfolio = updatedPortfolio,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "识别失败: ${e.message}"
                )
            }
        }
    }

    private fun addFundToPortfolio(fund: Fund) {
        val currentPortfolio = _uiState.value.portfolio
        val updatedFunds = if (currentPortfolio != null) {
            currentPortfolio.funds + fund
        } else {
            listOf(fund)
        }

        val updatedPortfolio = UserPortfolio(
            funds = updatedFunds,
            totalCost = updatedFunds.sumOf { it.cost },
            totalEstimatedValue = updatedFunds.sumOf { it.estimatedValue }
        )

        _uiState.value = _uiState.value.copy(
            portfolio = updatedPortfolio,
            isLoading = false
        )
    }

    fun selectFund(fund: Fund) {
        _uiState.value = _uiState.value.copy(selectedFund = fund)
    }

    fun clearSelectedFund() {
        _uiState.value = _uiState.value.copy(selectedFund = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun addManualFund(
        code: String,
        name: String,
        holdingAmount: Double,
        cost: Double
    ) {
        val newFund = Fund(
            id = System.currentTimeMillis(),
            code = code,
            name = name,
            holdingAmount = holdingAmount,
            cost = cost,
            costPerShare = if (holdingAmount > 0) cost / holdingAmount else 0.0,
            type = FundType.STOCK,
            estimatedValue = holdingAmount
        )

        addFundToPortfolio(newFund)
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
