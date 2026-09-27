package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.data.network.TelegramService
import com.example.data.repository.TradeRepository
import com.example.ui.TradeViewModel
import com.example.ui.TradeViewModelFactory
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignalScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    SIGNAL("Signal", Icons.Filled.Send, Icons.Outlined.Send, "nav_signal"),
    HISTORY("Historique", Icons.Filled.History, Icons.Outlined.History, "nav_history"),
    SETTINGS("Réglages", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val telegramService = TelegramService()
        val repository = TradeRepository(database.tradeDao(), telegramService, applicationContext)
        val factory = TradeViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val tradeViewModel: TradeViewModel = viewModel(factory = factory)
                val uiState by tradeViewModel.uiState.collectAsStateWithLifecycle()

                var currentScreen by remember { mutableStateOf(NavScreen.SIGNAL) }

                // BackHandler returns to Signal tab if on other tabs
                if (currentScreen != NavScreen.SIGNAL) {
                    BackHandler {
                        currentScreen = NavScreen.SIGNAL
                    }
                }

                TradeBotApp(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it },
                    uiState = uiState,
                    viewModel = tradeViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeBotApp(
    currentScreen: NavScreen,
    onNavigate: (NavScreen) -> Unit,
    uiState: com.example.ui.TradeUiState,
    viewModel: TradeViewModel
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GoldSignal",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = GoldPrimary,
                            letterSpacing = 0.5.sp
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BuyGreen, CircleShape)
                        )
                    }
                },
                actions = {
                    // Trades counter badge in top app bar
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Trades: ${uiState.todaySuccessfulCount}/${uiState.maxDailyTrades}",
                            color = if (uiState.isLimitReached) com.example.ui.theme.WarningOrange else GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = GoldPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                contentColor = TextPrimary,
                tonalElevation = 8.dp
            ) {
                NavScreen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigate(screen) },
                        icon = {
                            if (screen == NavScreen.HISTORY && uiState.historyList.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = GoldPrimary,
                                            contentColor = Color.Black
                                        ) {
                                            Text(
                                                text = "${uiState.historyList.size}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(screen.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        NavScreen.SIGNAL -> SignalScreen(
                            uiState = uiState,
                            viewModel = viewModel
                        )
                        NavScreen.HISTORY -> HistoryScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onSelectTradeToLoad = {
                                onNavigate(NavScreen.SIGNAL)
                            }
                        )
                        NavScreen.SETTINGS -> SettingsScreen(
                            uiState = uiState,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
