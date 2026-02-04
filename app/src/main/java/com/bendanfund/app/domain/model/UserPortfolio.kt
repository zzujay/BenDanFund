package com.bendanfund.app.domain.model

data class UserPortfolio(
    val id: Long = 0,
    val name: String = "我的持仓",
    val funds: List<Fund> = emptyList(),
    val totalCost: Double = 0.0,
    val totalEstimatedValue: Double = 0.0,
    val lastUpdateTime: Long = System.currentTimeMillis()
) {
    val totalProfit: Double
        get() = totalEstimatedValue - totalCost

    val totalProfitRate: Double
        get() = if (totalCost > 0) (totalProfit / totalCost) * 100 else 0.0

    val isTotalProfit: Boolean
        get() = totalProfit >= 0
}
