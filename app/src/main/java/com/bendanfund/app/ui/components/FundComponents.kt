package com.bendanfund.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bendanfund.app.domain.model.Fund
import com.bendanfund.app.ui.theme.Green500
import com.bendanfund.app.ui.theme.LossColor
import com.bendanfund.app.ui.theme.ProfitColor
import com.bendanfund.app.utils.FundParser

@Composable
fun FundCard(
    fund: Fund,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fund.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fund.code,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                ProfitBadge(
                    profitRate = fund.profitRate,
                    profit = fund.profit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FundMetric(
                    label = "持仓金额",
                    value = "¥${FundParser.formatCurrency(fund.holdingAmount)}"
                )
                FundMetric(
                    label = "成本",
                    value = "¥${FundParser.formatCurrency(fund.cost)}"
                )
                FundMetric(
                    label = "持仓收益",
                    value = "${FundParser.formatProfitRate(fund.profitRate)}",
                    valueColor = if (fund.isProfit) ProfitColor else LossColor
                )
            }
        }
    }
}

@Composable
fun FundMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
fun ProfitBadge(
    profitRate: Double,
    profit: Double,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (profit >= 0) ProfitColor.copy(alpha = 0.1f) else LossColor.copy(alpha = 0.1f)
    val textColor = if (profit >= 0) ProfitColor else LossColor

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = FundParser.formatProfitRate(profitRate),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = "${if (profit >= 0) "+" else ""}¥${FundParser.formatCurrency(profit)}",
            fontSize = 12.sp,
            color = textColor
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Green500
        )
        action?.invoke()
    }
}
