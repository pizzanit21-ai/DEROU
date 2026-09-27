package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_signals")
data class TradeSignal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val asset: String,
    val orderType: String,
    val entryPrice: String,
    val stopLoss: String,
    val takeProfit: String,
    val riskRewardRatio: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String,
    val status: String, // "SUCCESS" or "FAILED"
    val errorMessage: String? = null,
    val telegramMessage: String = ""
)
