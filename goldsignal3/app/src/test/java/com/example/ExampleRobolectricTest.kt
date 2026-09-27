package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.TradeDao
import com.example.data.network.TelegramService
import com.example.data.repository.TradeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GoldSignal", appName)
  }

  @Test
  fun `test risk reward ratio calculation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val mockDao = object : TradeDao {
      override fun getAllTrades() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.TradeSignal>>()
      override fun getTradesForDate(date: String) = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.TradeSignal>>()
      override fun getSuccessfulTradesCountForDate(date: String) = kotlinx.coroutines.flow.emptyFlow<Int>()
      override suspend fun getSuccessfulTradesCountForDateSync(date: String) = 0
      override suspend fun insertTrade(trade: com.example.data.model.TradeSignal) = 1L
      override suspend fun deleteTrade(trade: com.example.data.model.TradeSignal) {}
      override suspend fun deleteById(id: Long) {}
      override suspend fun clearAll() {}
    }
    val repository = TradeRepository(mockDao, TelegramService(), context)
    // Buy: Entry = 2350, SL = 2340 (Risk = 10), TP = 2370 (Reward = 20) -> RR = 1:2.00
    val rr = repository.calculateRiskReward("ACHAT (BUY)", "2350.00", "2340.00", "2370.00")
    assertEquals("1:2.00", rr)
  }
}
