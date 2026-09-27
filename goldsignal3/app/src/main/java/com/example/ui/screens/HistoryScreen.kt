package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TradeSignal
import com.example.ui.TradeUiState
import com.example.ui.TradeViewModel
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.BuyGreenContainer
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InputBackground
import com.example.ui.theme.SellRed
import com.example.ui.theme.SellRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    uiState: TradeUiState,
    viewModel: TradeViewModel,
    onSelectTradeToLoad: (TradeSignal) -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredList = remember(uiState.historyList, selectedFilter) {
        if (selectedFilter == "ALL") {
            uiState.historyList
        } else {
            uiState.historyList.filter { it.asset.contains(selectedFilter, ignoreCase = true) }
        }
    }

    val totalCount = uiState.historyList.size
    val successCount = uiState.historyList.count { it.status == "SUCCESS" }
    val failedCount = uiState.historyList.count { it.status == "FAILED" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Journal des Trades",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "$totalCount signaux enregistrés",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            if (totalCount > 0) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("btn_clear_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Tout effacer",
                        tint = TextTertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary Statistics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "Total",
                value = "$totalCount",
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Succès",
                value = "$successCount",
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Échecs",
                value = "$failedCount",
                color = if (failedCount > 0) SellRed else TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips
        val filterOptions = listOf("ALL" to "Tous", "XAUUSD" to "🥇 XAUUSD", "EURUSD" to "💱 EURUSD", "BTC" to "₿ BTC")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { (key, title) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    label = { Text(title, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = CardSurfaceVariant,
                        labelColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, if (selectedFilter == key) GoldPrimary else BorderColor),
                    modifier = Modifier.testTag("filter_chip_$key")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (totalCount == 0) "Aucun signal envoyé pour l'instant" else "Aucun signal pour ce filtre",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Les signaux exécutés depuis l'écran principal apparaîtront ici.",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.id }) { trade ->
                    TradeHistoryItemCard(
                        trade = trade,
                        onReuse = {
                            viewModel.populateFromTrade(trade)
                            onSelectTradeToLoad(trade)
                        },
                        onDelete = { viewModel.deleteTrade(trade) }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Effacer tout l'historique ?", color = TextPrimary) },
            text = {
                Text(
                    "Cette action supprimera définitivement tous les signaux de trade enregistrés localement.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SellRed)
                ) {
                    Text("Supprimer tout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun TradeHistoryItemCard(
    trade: TradeSignal,
    onReuse: () -> Unit,
    onDelete: () -> Unit
) {
    val isBuy = trade.orderType.contains("ACHAT") || trade.orderType.contains("BUY")
    val isSuccess = trade.status == "SUCCESS"
    val formattedDate = remember(trade.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(trade.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${trade.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(
            1.dp,
            if (isSuccess) BorderColor.copy(alpha = 0.4f) else SellRed.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Asset + Type badge + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trade.asset,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isBuy) BuyGreenContainer else SellRedContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isBuy) BuyGreen else SellRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBuy) "BUY" else "SELL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBuy) BuyGreen else SellRed
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSuccess) SuccessGreen.copy(alpha = 0.15f) else SellRed.copy(alpha = 0.15f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isSuccess) SuccessGreen else SellRed,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSuccess) "Envoyé" else "Échec",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSuccess) SuccessGreen else SellRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Details Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ENTRÉE", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Text(
                        text = if (trade.entryPrice.isNotBlank()) trade.entryPrice else "-",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Column {
                    Text("STOP LOSS", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Text(
                        text = if (trade.stopLoss.isNotBlank()) trade.stopLoss else "-",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SellRed
                    )
                }
                Column {
                    Text("TAKE PROFIT", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Text(
                        text = if (trade.takeProfit.isNotBlank()) trade.takeProfit else "-",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BuyGreen
                    )
                }
                if (trade.riskRewardRatio.isNotBlank()) {
                    Column {
                        Text("R : R", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Text(
                            text = trade.riskRewardRatio,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }
                }
            }

            // Error notice if failed
            if (!trade.errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Erreur : ${trade.errorMessage}",
                    fontSize = 11.sp,
                    color = SellRed,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Timestamp + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextTertiary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onReuse,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Recharger",
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recharger", fontSize = 11.sp, color = GoldPrimary)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
