package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TradeUiState
import com.example.ui.TradeViewModel
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InputBackground
import com.example.ui.theme.SellRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SettingsScreen(
    uiState: TradeUiState,
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var botToken by remember(uiState.settings.botToken) { mutableStateOf(uiState.settings.botToken) }
    var chatId by remember(uiState.settings.chatId) { mutableStateOf(uiState.settings.chatId) }
    var maxDailyTrades by remember(uiState.settings.maxTradesPerDay) {
        mutableIntStateOf(uiState.settings.maxTradesPerDay)
    }
    var isTokenVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "Configuration Telegram",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Gérez la connexion à votre bot Telegram et les règles d'exécution",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Telegram Channel Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2833)),
            border = BorderStroke(1.dp, TelegramBlue.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Telegram",
                        tint = TelegramBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Canal actif",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = chatId,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                if (chatId.startsWith("@")) {
                    OutlinedButton(
                        onClick = {
                            val channelName = chatId.removePrefix("@")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$channelName"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, TelegramBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TelegramBlue),
                        modifier = Modifier.testTag("btn_open_telegram")
                    ) {
                        Text("Ouvrir", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }

        // Credentials Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Identifiants du Bot",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )

                // Bot Token Input
                Column {
                    Text(
                        text = "Telegram Bot Token",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = botToken,
                        onValueChange = { botToken = it },
                        placeholder = { Text("Ex: 123456789:ABCDef...", color = TextTertiary, fontSize = 13.sp) },
                        visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                                Icon(
                                    imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isTokenVisible) "Masquer" else "Afficher",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_bot_token")
                    )
                }

                // Chat ID Input
                Column {
                    Text(
                        text = "Chat ID ou @NomDuCanal",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = chatId,
                        onValueChange = { chatId = it },
                        placeholder = { Text("Ex: @tradingma2026 ou -100123456789", color = TextTertiary, fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_chat_id")
                    )
                }

                // Max Trades Per Day Counter
                Column {
                    Text(
                        text = "Limite stricte journalière (trades / jour)",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(InputBackground, RoundedCornerShape(8.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$maxDailyTrades trade${if (maxDailyTrades > 1) "s" else ""} / jour",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (maxDailyTrades > 1) maxDailyTrades-- },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(CardSurfaceVariant, RoundedCornerShape(6.dp))
                                    .testTag("btn_decrease_limit")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Diminuer", tint = TextPrimary, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = { if (maxDailyTrades < 20) maxDailyTrades++ },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(CardSurfaceVariant, RoundedCornerShape(6.dp))
                                    .testTag("btn_increase_limit")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Augmenter", tint = TextPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Save button
                Button(
                    onClick = {
                        viewModel.saveSettings(botToken, chatId, maxDailyTrades)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BuyGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_settings")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer les Paramètres", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Test Connection Button
                OutlinedButton(
                    onClick = { viewModel.testConnection() },
                    enabled = !uiState.isTestingConnection,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_test_connection")
                ) {
                    if (uiState.isTestingConnection) {
                        CircularProgressIndicator(
                            color = GoldPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vérification en cours...", fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tester la connexion au Bot Telegram", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                // Test connection feedback message
                if (!uiState.testConnectionMessage.isNullOrBlank()) {
                    val isSuccess = uiState.testConnectionSuccess == true
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSuccess) Color(0xFF1B3320) else Color(0xFF3B1B1B),
                        border = BorderStroke(1.dp, if (isSuccess) SuccessGreen else SellRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isSuccess) SuccessGreen else SellRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.testConnectionMessage,
                                fontSize = 12.sp,
                                color = if (isSuccess) SuccessGreen else SellRed
                            )
                        }
                    }
                }
            }
        }

        // Restore Defaults
        OutlinedButton(
            onClick = {
                viewModel.restoreDefaults()
                botToken = uiState.settings.botToken
                chatId = uiState.settings.chatId
                maxDailyTrades = uiState.settings.maxTradesPerDay
            },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_restore_defaults")
        ) {
            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Restaurer les valeurs initiales", fontSize = 13.sp)
        }

        // Instructions Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Guide de configuration Telegram",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "1. Le bot Telegram doit être administrateur de votre canal avec la permission 'Poster des messages'.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "2. Pour un canal public, utilisez '@NomDuCanal'. Pour un groupe privé, utilisez l'identifiant numérique commençant par -100.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "3. La limite stricte journalière empêche l'over-trading et discipline votre stratégie de risque.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
