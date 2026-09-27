package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.TradeUiState
import com.example.ui.TradeViewModel
import com.example.ui.components.AssetSelector
import com.example.ui.components.DailyLimitCard
import com.example.ui.components.OrderTypeToggle
import com.example.ui.components.RiskRewardDisplay
import com.example.ui.components.StatusFeedbackBanner
import com.example.ui.components.TradeInputField
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningOrange

private data class QuickPreset(
    val name: String,
    val asset: String,
    val assetLabel: String,
    val type: String,
    val entry: String,
    val sl: String,
    val tp: String
)

private val SAMPLE_PRESETS = listOf(
    QuickPreset("XAUUSD Scalp Buy", "XAUUSD", "🥇 XAUUSD (Or)", "ACHAT (BUY)", "2350.50", "2342.00", "2368.00"),
    QuickPreset("XAUUSD Swing Sell", "XAUUSD", "🥇 XAUUSD (Or)", "VENTE (SELL)", "2380.00", "2390.00", "2355.00"),
    QuickPreset("EURUSD Day Buy", "EURUSD", "💱 EURUSD (Forex)", "ACHAT (BUY)", "1.08500", "1.08150", "1.09200"),
    QuickPreset("BTCUSDT Breakout", "BTCUSDT", "₿ BTCUSDT (Crypto)", "ACHAT (BUY)", "65200", "64100", "67800")
)

@Composable
fun SignalScreen(
    uiState: TradeUiState,
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header with Trading Candlestick Art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_gold_trading),
                contentDescription = "Graphique XAUUSD Gold",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color(0xFF121212)
                            )
                        )
                    )
            )

            // Header Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BOT TELEGRAM XAUUSD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = BuyGreen.copy(alpha = 0.2f),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(BuyGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BuyGreen
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Signal de Trade",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF242424),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Text(
                            text = uiState.settings.chatId,
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Daily Limit Tracker Card
            DailyLimitCard(
                usedCount = uiState.todaySuccessfulCount,
                maxLimit = uiState.maxDailyTrades
            )

            // Status message banner if any
            StatusFeedbackBanner(
                message = uiState.statusMessage,
                statusType = uiState.statusType,
                onDismiss = { viewModel.dismissStatus() }
            )

            // Quick Setups / Presets Bar
            Column {
                Text(
                    text = "Configurations rapides",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SAMPLE_PRESETS) { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardSurfaceVariant,
                            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.onAssetSelected(preset.asset, preset.assetLabel)
                                    viewModel.onOrderTypeSelected(preset.type)
                                    viewModel.onEntryChanged(preset.entry)
                                    viewModel.onSlChanged(preset.sl)
                                    viewModel.onTpChanged(preset.tp)
                                }
                                .testTag("preset_${preset.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = preset.name,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Main Trade Signal Form Card (matching user's HTML card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trade_form_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Asset Selection
                    AssetSelector(
                        selectedAsset = uiState.selectedAsset,
                        onAssetSelected = { id, label ->
                            viewModel.onAssetSelected(id, label)
                        }
                    )

                    // Order Type (Buy / Sell)
                    OrderTypeToggle(
                        selectedType = uiState.selectedOrderType,
                        onTypeSelected = { viewModel.onOrderTypeSelected(it) }
                    )

                    // Entry Price
                    TradeInputField(
                        label = "Prix d'entrée (Entry)",
                        value = uiState.entryPrice,
                        onValueChange = { viewModel.onEntryChanged(it) },
                        placeholder = "Ex: 2350.50 ou 1.0850",
                        testTag = "input_entry"
                    )

                    // Stop Loss (SL)
                    TradeInputField(
                        label = "Stop Loss (SL)",
                        value = uiState.stopLoss,
                        onValueChange = { viewModel.onSlChanged(it) },
                        placeholder = "Ex: 2340.00 ou 1.0800",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        testTag = "input_sl"
                    )

                    // Take Profit (TP)
                    TradeInputField(
                        label = "Take Profit (TP)",
                        value = uiState.takeProfit,
                        onValueChange = { viewModel.onTpChanged(it) },
                        placeholder = "Ex: 2370.00 ou 1.0950",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = BuyGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        testTag = "input_tp"
                    )

                    // Live R:R Ratio Calculation
                    RiskRewardDisplay(riskReward = uiState.calculatedRR)

                    Spacer(modifier = Modifier.height(4.dp))

                    // Execute & Send Button
                    val isExecuteEnabled = !uiState.isLoading && !uiState.isLimitReached

                    Button(
                        onClick = { viewModel.sendTrade() },
                        enabled = isExecuteEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BuyGreen,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF2C3E2F),
                            disabledContentColor = Color(0xFF888888)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_send_trade")
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Envoi vers Telegram...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (uiState.isLimitReached) {
                            Text(
                                text = "Limite journalière atteinte (2/2)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningOrange
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Envoyer",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exécuter et Envoyer sur Telegram",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (uiState.isLimitReached) {
                        Text(
                            text = "ℹ️ Vous pouvez réinitialiser ou ajuster votre limite dans l'onglet Configuration.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
