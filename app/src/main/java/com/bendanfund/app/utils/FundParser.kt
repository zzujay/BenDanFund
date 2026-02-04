package com.bendanfund.app.utils

import com.bendanfund.app.domain.model.Fund
import com.bendanfund.app.domain.model.FundType
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

object FundParser {
    private val fundCodePattern = Pattern.compile("(\\d{6})")
    private val amountPattern = Pattern.compile("([\\d,]+\\.?\\d*)\\s*(?:元|万)?")
    private val datePattern = Pattern.compile("(\\d{4}[-/]\\d{2}[-/]\\d{2})")

    fun parseAlipayFundScreenshot(text: String): List<Fund> {
        val funds = mutableListOf<Fund>()
        val lines = text.lines()

        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            val fundCode = extractFundCode(line)
            val fundName = extractFundName(line, funds)

            if (fundCode != null) {
                val holdingInfo = extractHoldingInfo(lines, i)
                if (holdingInfo != null) {
                    val fund = Fund(
                        code = fundCode,
                        name = holdingInfo.first,
                        holdingAmount = holdingInfo.second.first,
                        cost = holdingInfo.second.second,
                        costPerShare = holdingInfo.second.third,
                        type = determineFundType(fundCode)
                    )
                    funds.add(fund)
                }
            }
            i++
        }

        return funds
    }

    private fun extractFundCode(line: String): String? {
        val matcher = fundCodePattern.matcher(line)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }

    private fun extractFundName(line: String, existingFunds: List<Fund>): String {
        val parts = line.split("\\s+".toRegex())
        return parts.firstOrNull { it.length in 2..10 && !it.matches(Regex("\\d+")) }
            ?: ""
    }

    private fun extractHoldingInfo(
        lines: List<String>,
        startIndex: Int
    ): Pair<String, Pair<Double, Double, Double>>? {
        if (startIndex + 1 >= lines.size) return null

        val nextLine = lines[startIndex + 1]
        val amountMatcher = amountPattern.matcher(nextLine)

        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "")?.toDoubleOrNull() ?: return null
            val costStr = if (amountMatcher.find()) {
                amountMatcher.group(1)?.replace(",", "")?.toDoubleOrNull() ?: 0.0
            } else 0.0

            val costPerShare = if (costStr > 0) costStr / amountStr else 0.0
            return Pair("", Pair(amountStr, costStr, costPerShare))
        }
        return null
    }

    private fun determineFundType(code: String): FundType {
        return when (code.firstOrNull()) {
            '1' -> FundType.STOCK
            '5' -> FundType.QDII
            '3' -> FundType.BOND
            else -> FundType.MIXED
        }
    }

    fun parseAmount(text: String): Double {
        val cleanedText = text.replace(",", "").replace(" ", "")
        return cleanedText.toDoubleOrNull() ?: 0.0
    }

    fun formatCurrency(amount: Double): String {
        return String.format("%.2f", amount)
    }

    fun formatProfitRate(rate: Double): String {
        return String.format("%+.2f%%", rate)
    }
}
