package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.TradeDao
import com.example.data.model.TradeSignal
import com.example.data.network.TelegramResult
import com.example.data.network.TelegramService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

data class TelegramSettings(
    val botToken: String,
    val chatId: String,
    val maxTradesPerDay: Int
)

class TradeRepository(
    private val tradeDao: TradeDao,
    private val telegramService: TelegramService,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("trade_bot_prefs", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_BOT_TOKEN = "8562240682:AAG2DNYlahI8HGmt1LWm7AUoVqQLzx84z8s"
        const val DEFAULT_CHAT_ID = "@tradingma2026"
        const val DEFAULT_MAX_TRADES = 2

        private const val KEY_BOT_TOKEN = "key_bot_token"
        private const val KEY_CHAT_ID = "key_chat_id"
        private const val KEY_MAX_TRADES = "key_max_trades"
    }

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState = _settingsState.asStateFlow()

    private fun loadSettings(): TelegramSettings {
        return TelegramSettings(
            botToken = prefs.getString(KEY_BOT_TOKEN, DEFAULT_BOT_TOKEN) ?: DEFAULT_BOT_TOKEN,
            chatId = prefs.getString(KEY_CHAT_ID, DEFAULT_CHAT_ID) ?: DEFAULT_CHAT_ID,
            maxTradesPerDay = prefs.getInt(KEY_MAX_TRADES, DEFAULT_MAX_TRADES)
        )
    }

    fun saveSettings(botToken: String, chatId: String, maxTradesPerDay: Int) {
        prefs.edit()
            .putString(KEY_BOT_TOKEN, botToken.trim())
            .putString(KEY_CHAT_ID, chatId.trim())
            .putInt(KEY_MAX_TRADES, maxTradesPerDay.coerceAtLeast(1))
            .apply()

        _settingsState.value = TelegramSettings(
            botToken = botToken.trim(),
            chatId = chatId.trim(),
            maxTradesPerDay = maxTradesPerDay.coerceAtLeast(1)
        )
    }

    fun restoreDefaultSettings() {
        saveSettings(DEFAULT_BOT_TOKEN, DEFAULT_CHAT_ID, DEFAULT_MAX_TRADES)
    }

    fun getAllTrades(): Flow<List<TradeSignal>> = tradeDao.getAllTrades()

    fun getTradesForToday(todayDate: String): Flow<List<TradeSignal>> =
        tradeDao.getTradesForDate(todayDate)

    fun getSuccessfulTradesCountToday(todayDate: String): Flow<Int> =
        tradeDao.getSuccessfulTradesCountForDate(todayDate)

    suspend fun getSuccessfulTradesCountTodaySync(todayDate: String): Int =
        tradeDao.getSuccessfulTradesCountForDateSync(todayDate)

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun formatCurrentDateTime(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun calculateRiskReward(orderType: String, entryStr: String, slStr: String, tpStr: String): String {
        val entry = entryStr.toDoubleOrNull() ?: return ""
        val sl = slStr.toDoubleOrNull() ?: return ""
        val tp = tpStr.toDoubleOrNull() ?: return ""

        val isBuy = orderType.uppercase().contains("ACHAT") || orderType.uppercase().contains("BUY")

        val risk = if (isBuy) (entry - sl) else (sl - entry)
        val reward = if (isBuy) (tp - entry) else (entry - tp)

        if (risk <= 0.0 || reward <= 0.0) {
            return ""
        }

        val ratio = reward / risk
        return "1:%.2f".format(Locale.US, ratio)
    }

    fun buildTelegramMessage(
        asset: String,
        orderType: String,
        entry: String,
        sl: String,
        tp: String,
        riskReward: String
    ): String {
        val dateFormatted = formatCurrentDateTime()
        val entryFormatted = if (entry.isNotBlank()) entry.trim() else "Non spécifié"
        val slFormatted = if (sl.isNotBlank()) sl.trim() else "Non spécifié"
        val tpFormatted = if (tp.isNotBlank()) tp.trim() else "Non spécifié"

        val sb = StringBuilder()
        sb.append("🚨 *NOUVEAU SIGNAL DE TRADE* 🚨\n\n")
        sb.append("📊 *Actif :* $asset\n")
        sb.append("📈 *Ordre :* $orderType\n")
        sb.append("💰 *Entrée :* $entryFormatted\n")
        sb.append("🛑 *SL :* $slFormatted\n")
        sb.append("🎯 *TP :* $tpFormatted\n")

        if (riskReward.isNotBlank()) {
            sb.append("⚖️ *Risk / Reward :* $riskReward\n")
        }

        sb.append("\n📅 *Date :* $dateFormatted")
        return sb.toString()
    }

    suspend fun sendTradeSignal(
        asset: String,
        orderType: String,
        entry: String,
        sl: String,
        tp: String
    ): Pair<TelegramResult, TradeSignal> {
        val today = getTodayDateString()
        val settings = _settingsState.value
        val rr = calculateRiskReward(orderType, entry, sl, tp)
        val message = buildTelegramMessage(asset, orderType, entry, sl, tp, rr)

        val result = telegramService.sendMessage(
            botToken = settings.botToken,
            chatId = settings.chatId,
            text = message,
            parseMode = "Markdown"
        )

        val status = when (result) {
            is TelegramResult.Success -> "SUCCESS"
            is TelegramResult.Error -> "FAILED"
        }

        val errorMessage = when (result) {
            is TelegramResult.Success -> null
            is TelegramResult.Error -> result.message
        }

        val trade = TradeSignal(
            asset = asset,
            orderType = orderType,
            entryPrice = entry.trim(),
            stopLoss = sl.trim(),
            takeProfit = tp.trim(),
            riskRewardRatio = rr,
            timestamp = System.currentTimeMillis(),
            dateString = today,
            status = status,
            errorMessage = errorMessage,
            telegramMessage = message
        )

        val id = tradeDao.insertTrade(trade)
        return Pair(result, trade.copy(id = id))
    }

    suspend fun testConnection(): TelegramResult {
        val settings = _settingsState.value
        return telegramService.testConnection(settings.botToken, settings.chatId)
    }

    suspend fun deleteTrade(trade: TradeSignal) {
        tradeDao.deleteTrade(trade)
    }

    suspend fun clearHistory() {
        tradeDao.clearAll()
    }
}
