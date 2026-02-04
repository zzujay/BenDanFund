package com.bendanfund.app

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bendanfund.app.ui.screens.HomeScreen
import com.bendanfund.app.ui.screens.MainViewModel
import com.bendanfund.app.ui.screens.UploadScreen
import com.bendanfund.app.ui.theme.BenDanFundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BenDanFundTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5)
                ) {
                    BenDanFundApp()
                }
            }
        }
    }
}

@Composable
fun BenDanFundApp(
    viewModel: MainViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val uiState by viewModel.uiState.collectAsState()

    when (currentScreen) {
        Screen.Home -> {
            HomeScreen(
                portfolio = uiState.portfolio,
                isLoading = uiState.isLoading || uiState.isRefreshing,
                onAddFund = { currentScreen = Screen.Upload },
                onFundClick = { fundCode ->
                    uiState.portfolio?.funds?.find { it.code == fundCode }?.let {
                        viewModel.selectFund(it)
                    }
                },
                onRefresh = { viewModel.refreshEstimates() }
            )
        }

        Screen.Upload -> {
            UploadScreen(
                onImageSelected = { bitmap ->
                    viewModel.processScreenshot(bitmap)
                    currentScreen = Screen.Home
                },
                onNavigateBack = { currentScreen = Screen.Home }
            )
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data object Upload : Screen()
}
