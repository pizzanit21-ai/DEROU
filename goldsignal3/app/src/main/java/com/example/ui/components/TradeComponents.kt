package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StatusType
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
import com.example.ui.theme.WarningOrange

data class AssetOption(val id: String, val label: String, val icon: String)

val DEFAULT_ASSETS = listOf(
    AssetOption("XAUUSD", "🥇 XAUUSD (Or)", "🥇"),
    AssetOption("EURUSD", "💱 EURUSD (Forex)", "💱"),
    AssetOption("BTCUSDT", "₿ BTCUSDT (Crypto)", "₿"),
    AssetOption("US30", "📈 US30 (Dow Jones)", "📈"),
    AssetOption("NAS100", "⚡ NAS100 (Nasdaq)", "⚡"),
    AssetOption("GBPUSD", "💷 GBPUSD (Forex)", "💷")
)

@Composable
fun DailyLimitCard(
    usedCount: Int,
    maxLimit: Int,
    modifier: Modifier = Modifier
) {
    val progress = (usedCount.toFloat() / maxLimit.toFloat()).coerceIn(0f, 1f)
    val isLimitReached = usedCount >= maxLimit

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_limit_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLimitReached) Color(0xFF2C1E14) else CardSurfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (isLimitReached) WarningOrange.copy(alpha = 0.6f) else BorderColor.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isLimitReached) Icons.Default.Warning else Icons.Default.Shield,
                        contentDescription = "Limite",
                        tint = if (isLimitReached) WarningOrange else GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Limite journalière stricte",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isLimitReached) WarningOrange.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$usedCount / $maxLimit trades",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLimitReached) WarningOrange else GoldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isLimitReached) WarningOrange else GoldPrimary,
                trackColor = InputBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isLimitReached) {
                    "⚠️ Limite quotidienne atteinte. Aucun nouveau trade ne peut être envoyé aujourd'hui."
                } else {
                    "${maxLimit - usedCount} trade(s) restant(s) pour aujourd'hui."
                },
                fontSize = 11.sp,
                color = if (isLimitReached) WarningOrange else TextSecondary
            )
        }
    }
}

@Composable
fun AssetSelector(
    selectedAsset: String,
    onAssetSelected: (id: String, label: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentOption = DEFAULT_ASSETS.find { it.id == selectedAsset } ?: DEFAULT_ASSETS.first()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Actif",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(InputBackground, RoundedCornerShape(8.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp)
                .testTag("asset_selector"),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = currentOption.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = currentOption.label,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Menu déroulant actif",
                    tint = TextSecondary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(CardBackground)
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            ) {
                DEFAULT_ASSETS.forEach { asset ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(asset.icon, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    asset.label,
                                    color = if (asset.id == selectedAsset) GoldPrimary else TextPrimary,
                                    fontWeight = if (asset.id == selectedAsset) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            onAssetSelected(asset.id, asset.label)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderTypeToggle(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBuy = selectedType.contains("ACHAT") || selectedType.contains("BUY")

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Type d'ordre",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(InputBackground, RoundedCornerShape(8.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // BUY Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isBuy) BuyGreen else Color.Transparent)
                    .clickable { onTypeSelected("ACHAT (BUY)") }
                    .testTag("order_type_buy"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Buy",
                        tint = if (isBuy) Color.White else BuyGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACHAT (BUY)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isBuy) Color.White else TextSecondary
                    )
                }
            }

            // SELL Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (!isBuy) SellRed else Color.Transparent)
                    .clickable { onTypeSelected("VENTE (SELL)") }
                    .testTag("order_type_sell"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Sell",
                        tint = if (!isBuy) Color.White else SellRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VENTE (SELL)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (!isBuy) Color.White else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun TradeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextTertiary, fontSize = 14.sp) },
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Effacer",
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                disabledContainerColor = InputBackground,
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
fun RiskRewardDisplay(
    riskReward: String,
    modifier: Modifier = Modifier
) {
    if (riskReward.isNotEmpty()) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GoldPrimary.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚖️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ratio Risque / Récompense :",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
                Text(
                    text = riskReward,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }
        }
    }
}

@Composable
fun StatusFeedbackBanner(
    message: String?,
    statusType: StatusType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (!message.isNullOrBlank()) {
            val (bgColor, borderColor, textColor, icon) = when (statusType) {
                StatusType.SUCCESS -> Quad(
                    Color(0xFF1B3320),
                    SuccessGreen.copy(alpha = 0.6f),
                    SuccessGreen,
                    Icons.Default.CheckCircle
                )
                StatusType.ERROR -> Quad(
                    Color(0xFF3B1B1B),
                    SellRed.copy(alpha = 0.6f),
                    SellRed,
                    Icons.Default.Error
                )
                StatusType.WARNING -> Quad(
                    Color(0xFF382A16),
                    WarningOrange.copy(alpha = 0.6f),
                    WarningOrange,
                    Icons.Default.Warning
                )
                StatusType.INFO -> Quad(
                    CardSurfaceVariant,
                    BorderColor,
                    TextPrimary,
                    Icons.Default.Info
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = bgColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("status_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Status icon",
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = message,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Fermer",
                            tint = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
