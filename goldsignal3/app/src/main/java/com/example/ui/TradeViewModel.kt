package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.TradeSignal
import com.example.data.network.TelegramResult
import com.example.data.repository.TelegramSettings
import com.example.data.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StatusType {
    INFO, SUCCESS, ERROR, WARNING
}

data class TradeUiState(
    val selectedAsset: String = "XAUUSD",
    val selectedAssetLabel: String = "🥇 XAUUSD (Or)",
    val selectedOrderType: String = "ACHAT (BUY)",
    val entryPrice: String = "",
    val stopLoss: String = "",
    val takeProfit: String = "",
    val calculatedRR: String = "",
    val todaySuccessfulCount: Int = 0,
    val maxDailyTrades: Int = 2,
    val isLimitReached: Boolean = false,
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val statusType: StatusType = StatusType.INFO,
    val historyList: List<TradeSignal> = emptyList(),
    val filterAsset: String = "ALL",
    val settings: TelegramSettings = TelegramSettings(
        TradeRepository.DEFAULT_BOT_TOKEN,
        TradeRepository.DEFAULT_CHAT_ID,
        TradeRepository.DEFAULT_MAX_TRADES
    ),
    val isTestingConnection: Boolean = false,
    val testConnectionMessage: String? = null,
    val testConnectionSuccess: Boolean? = null
)

class TradeViewModel(
    private val repository: TradeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TradeUiState())
    val uiState: StateFlow<TradeUiState> = _uiState.asStateFlow()

    init {
        val today = repository.getTodayDateString()

        viewModelScope.launch {
            repository.settingsState.collect { settings ->
                _uiState.update { current ->
                    val limitReached = current.todaySuccessfulCount >= settings.maxTradesPerDay
                    current.copy(
                        settings = settings,
                        maxDailyTrades = settings.maxTradesPerDay,
                        isLimitReached = limitReached
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getSuccessfulTradesCountToday(today).collect { count ->
                _uiState.update { current ->
                    val limitReached = count >= current.maxDailyTrades
                    current.copy(
                        todaySuccessfulCount = count,
                        isLimitReached = limitReached
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getAllTrades().collect { list ->
                _uiState.update { current ->
                    current.copy(historyList = list)
                }
            }
        }
    }

    fun onAssetSelected(asset: String, label: String) {
        _uiState.update { current ->
            current.copy(selectedAsset = asset, selectedAssetLabel = label)
        }
        recalculateRR()
    }

    fun onOrderTypeSelected(type: String) {
        _uiState.update { current ->
            current.copy(selectedOrderType = type)
        }
        recalculateRR()
    }

    fun onEntryChanged(value: String) {
        _uiState.update { it.copy(entryPrice = value) }
        recalculateRR()
    }

    fun onSlChanged(value: String) {
        _uiState.update { it.copy(stopLoss = value) }
        recalculateRR()
    }

    fun onTpChanged(value: String) {
        _uiState.update { it.copy(takeProfit = value) }
        recalculateRR()
    }

    private fun recalculateRR() {
        val current = _uiState.value
        val rr = repository.calculateRiskReward(
            current.selectedOrderType,
            current.entryPrice,
            current.stopLoss,
            current.takeProfit
        )
        _uiState.update { it.copy(calculatedRR = rr) }
    }

    fun sendTrade() {
        val current = _uiState.value
        val today = repository.getTodayDateString()

        viewModelScope.launch {
            val countToday = repository.getSuccessfulTradesCountTodaySync(today)
            if (countToday >= current.maxDailyTrades) {
                _uiState.update {
                    it.copy(
                        statusMessage = "⚠️ Limite atteinte : ${current.maxDailyTrades} trades déjà exécutés aujourd'hui !",
                        statusType = StatusType.WARNING,
                        isLimitReached = true
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    statusMessage = "Envoi du trade en cours vers Telegram...",
                    statusType = StatusType.INFO
                )
            }

            val (result, trade) = repository.sendTradeSignal(
                asset = current.selectedAssetLabel,
                orderType = current.selectedOrderType,
                entry = current.entryPrice,
                sl = current.stopLoss,
                tp = current.takeProfit
            )

            when (result) {
                is TelegramResult.Success -> {
                    val newCount = countToday + 1
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = "✅ Trade envoyé avec succès ! ($newCount/${it.maxDailyTrades} aujourd'hui)",
                            statusType = StatusType.SUCCESS,
                            todaySuccessfulCount = newCount,
                            isLimitReached = newCount >= it.maxDailyTrades,
                            entryPrice = "",
                            stopLoss = "",
                            takeProfit = "",
                            calculatedRR = ""
                        )
                    }
                }
                is TelegramResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = "❌ Erreur Telegram : ${result.message}",
                            statusType = StatusType.ERROR
                        )
                    }
                }
            }
        }
    }

    fun saveSettings(botToken: String, chatId: String, maxDailyTrades: Int) {
        repository.saveSettings(botToken, chatId, maxDailyTrades)
        _uiState.update {
            it.copy(
                statusMessage = "Paramètres Telegram enregistrés !",
                statusType = StatusType.SUCCESS
            )
        }
    }

    fun restoreDefaults() {
        repository.restoreDefaultSettings()
        _uiState.update {
            it.copy(
                statusMessage = "Paramètres réinitialisés par défaut.",
                statusType = StatusType.INFO
            )
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTestingConnection = true,
                    testConnectionMessage = "Test de communication avec Telegram...",
                    testConnectionSuccess = null
                )
            }
            val result = repository.testConnection()
            when (result) {
                is TelegramResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isTestingConnection = false,
                            testConnectionSuccess = true,
                            testConnectionMessage = "✅ Connexion réussie : ${result.rawResponse}"
                        )
                    }
                }
                is TelegramResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isTestingConnection = false,
                            testConnectionSuccess = false,
                            testConnectionMessage = "❌ Échec de connexion : ${result.message}"
                        )
                    }
                }
            }
        }
    }

    fun deleteTrade(trade: TradeSignal) {
        viewModelScope.launch {
            repository.deleteTrade(trade)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setFilterAsset(asset: String) {
        _uiState.update { it.copy(filterAsset = asset) }
    }

    fun populateFromTrade(trade: TradeSignal) {
        _uiState.update {
            it.copy(
                selectedAsset = trade.asset,
                selectedAssetLabel = trade.asset,
                selectedOrderType = trade.orderType,
                entryPrice = trade.entryPrice,
                stopLoss = trade.stopLoss,
                takeProfit = trade.takeProfit,
                calculatedRR = trade.riskRewardRatio,
                statusMessage = "Signal chargé depuis l'historique",
                statusType = StatusType.INFO
            )
        }
    }

    fun dismissStatus() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}

class TradeViewModelFactory(private val repository: TradeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TradeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TradeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
