package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TradeSignal
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trade_signals ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeSignal>>

    @Query("SELECT * FROM trade_signals WHERE dateString = :date ORDER BY timestamp DESC")
    fun getTradesForDate(date: String): Flow<List<TradeSignal>>

    @Query("SELECT COUNT(*) FROM trade_signals WHERE dateString = :date AND status = 'SUCCESS'")
    fun getSuccessfulTradesCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM trade_signals WHERE dateString = :date AND status = 'SUCCESS'")
    suspend fun getSuccessfulTradesCountForDateSync(date: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeSignal): Long

    @Delete
    suspend fun deleteTrade(trade: TradeSignal)

    @Query("DELETE FROM trade_signals WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM trade_signals")
    suspend fun clearAll()
}
