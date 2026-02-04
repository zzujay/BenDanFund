package com.bendanfund.app.domain.model

data class Fund(
    val id: Long = 0,
    val code: String,
    val name: String,
    val holdingAmount: Double,
    val cost: Double,
    val costPerShare: Double,
    val type: FundType = FundType.STOCK,
    val netValue: Double = 0.0,
    val estimatedValue: Double = 0.0,
    val lastUpdateTime: Long = System.currentTimeMillis()
) {
    val profit: Double
        get() = estimatedValue - cost

    val profitRate: Double
        get() = if (cost > 0) (profit / cost) * 100 else 0.0

    val isProfit: Boolean
        get() = profit >= 0
}

enum class FundType {
    STOCK,      // 股票型
    BOND,       // 债券型
    MIXED,      // 混合型
    INDEX,      // 指数型
    QDII,       // QDII
    CURRENCY    // 货币型
}

data class FundHolding(
    val fund: Fund,
    val shares: Double,
    val currentPrice: Double
)
