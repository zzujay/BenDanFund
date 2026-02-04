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
    val isRefreshing: Boolean = false
)

class MainViewModel : ViewModel() {
    private val repository = FundRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        loadMockData()
    }

    private fun loadMockData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            delay(500)

            val mockFunds = listOf(
                Fund(
                    id = 1,
                    code = "161039",
                    name = "富国中证新能源汽车指数",
                    holdingAmount = 15000.0,
                    cost = 12000.0,
                    costPerShare = 1.2,
                    type = FundType.INDEX,
                    estimatedValue = 16500.0,
                    netValue = 1.35
                ),
                Fund(
                    id = 2,
                    code = "110022",
                    name = "易方达消费行业股票",
                    holdingAmount = 25000.0,
                    cost = 28000.0,
                    costPerShare = 1.8,
                    type = FundType.STOCK,
                    estimatedValue = 26500.0,
                    netValue = 1.72
                ),
                Fund(
                    id = 3,
                    code = "162411",
                    name = "华宝标普油气上游股票",
                    holdingAmount = 8000.0,
                    cost = 7500.0,
                    costPerShare = 0.95,
                    type = FundType.STOCK,
                    estimatedValue = 8200.0,
                    netValue = 1.02
                )
            )

            val portfolio = UserPortfolio(
                name = "我的持仓",
                funds = mockFunds,
                totalCost = mockFunds.sumOf { it.cost },
                totalEstimatedValue = mockFunds.sumOf { it.estimatedValue }
            )

            _uiState.value = MainUiState(
                portfolio = portfolio,
                isLoading = false
            )
        }
    }

    fun processScreenshot(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                delay(1000)

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
                        estimatedValue = 10200.0
                    )

                    val currentPortfolio = _uiState.value.portfolio
                    val updatedFunds = if (currentPortfolio != null) {
                        currentPortfolio.funds + newFund
                    } else {
                        listOf(newFund)
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

    fun refreshEstimates() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)

            delay(2000)

            val currentPortfolio = _uiState.value.portfolio ?: return@launch

            val updatedFunds = currentPortfolio.funds.map { fund ->
                val variation = (Math.random() - 0.5) * 100
                fund.copy(
                    estimatedValue = fund.estimatedValue + variation,
                    lastUpdateTime = System.currentTimeMillis()
                )
            }

            val updatedPortfolio = currentPortfolio.copy(
                funds = updatedFunds,
                totalEstimatedValue = updatedFunds.sumOf { it.estimatedValue },
                lastUpdateTime = System.currentTimeMillis()
            )

            _uiState.value = _uiState.value.copy(
                portfolio = updatedPortfolio,
                isRefreshing = false
            )
        }
    }

    fun selectFund(fund: Fund) {
        _uiState.value = _uiState.value.copy(selectedFund = fund)
    }

    fun clearSelectedFund() {
        _uiState.value = _uiState.value.copy(selectedFund = null)
    }

    fun addManualFund(
        code: String,
        name: String,
        holdingAmount: Double,
        cost: Double
    ) {
        viewModelScope.launch {
            val newFund = Fund(
                id = System.currentTimeMillis(),
                code = code,
                name = name,
                holdingAmount = holdingAmount,
                cost = cost,
                costPerShare = if (holdingAmount > 0) cost / holdingAmount else 0.0,
                type = FundType.STOCK,
                estimatedValue = holdingAmount * 1.02
            )

            val currentPortfolio = _uiState.value.portfolio
            val updatedFunds = if (currentPortfolio != null) {
                currentPortfolio.funds + newFund
            } else {
                listOf(newFund)
            }

            val updatedPortfolio = UserPortfolio(
                funds = updatedFunds,
                totalCost = updatedFunds.sumOf { it.cost },
                totalEstimatedValue = updatedFunds.sumOf { it.estimatedValue }
            )

            _uiState.value = _uiState.value.copy(portfolio = updatedPortfolio)
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
