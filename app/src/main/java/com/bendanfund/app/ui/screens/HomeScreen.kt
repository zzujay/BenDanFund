package com.bendanfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bendanfund.app.domain.model.UserPortfolio
import com.bendanfund.app.ui.components.EmptyStateView
import com.bendanfund.app.ui.components.FundCard
import com.bendanfund.app.ui.components.LoadingView
import com.bendanfund.app.ui.components.PortfolioSummaryCard
import com.bendanfund.app.ui.components.SectionHeader
import com.bendanfund.app.ui.theme.BackgroundLight
import com.bendanfund.app.ui.theme.Green500
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    portfolio: UserPortfolio?,
    isLoading: Boolean,
    onAddFund: () -> Unit,
    onFundClick: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "笨蛋基基",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            isRefreshing = true
                            onRefresh()
                            scope.launch {
                                delay(1000)
                                isRefreshing = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green500,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFund,
                containerColor = Green500,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加基金"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            when {
                isLoading -> LoadingView()

                portfolio == null || portfolio.funds.isEmpty() -> {
                    EmptyStateView(
                        title = "还没有持仓基金",
                        subtitle = "点击下方按钮上传支付宝基金截图",
                        onAction = onAddFund,
                        actionLabel = "上传截图"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            PortfolioSummaryCard(portfolio = portfolio)
                        }

                        item {
                            SectionHeader(
                                title = "持仓明细",
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        items(
                            items = portfolio.funds,
                            key = { it.id }
                        ) { fund ->
                            FundCard(
                                fund = fund,
                                onClick = { onFundClick(fund.code) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}
