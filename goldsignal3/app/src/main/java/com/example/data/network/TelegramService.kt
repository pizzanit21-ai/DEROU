package com.example.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class TelegramResult {
    data class Success(val messageId: Long? = null, val rawResponse: String) : TelegramResult()
    data class Error(val message: String, val errorCode: Int? = null) : TelegramResult()
}

class TelegramService {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(
        botToken: String,
        chatId: String,
        text: String,
        parseMode: String = "Markdown"
    ): TelegramResult = withContext(Dispatchers.IO) {
        val cleanToken = botToken.trim()
        val cleanChatId = chatId.trim()

        if (cleanToken.isEmpty()) {
            return@withContext TelegramResult.Error("Le Token du Bot Telegram est manquant.")
        }
        if (cleanChatId.isEmpty()) {
            return@withContext TelegramResult.Error("Le Chat ID / Canal Telegram est manquant.")
        }

        val url = "https://api.telegram.org/bot$cleanToken/sendMessage"

        try {
            val jsonObject = JSONObject().apply {
                put("chat_id", cleanChatId)
                put("text", text)
                put("parse_mode", parseMode)
            }

            val body = jsonObject.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                if (json.optBoolean("ok", false)) {
                    val result = json.optJSONObject("result")
                    val messageId = result?.optLong("message_id")
                    TelegramResult.Success(messageId = messageId, rawResponse = responseBody)
                } else {
                    val desc = json.optString("description", "Erreur Telegram inconnue")
                    TelegramResult.Error(desc, json.optInt("error_code", response.code))
                }
            } else {
                val errorDesc = try {
                    val json = JSONObject(responseBody)
                    json.optString("description", "Erreur HTTP ${response.code}")
                } catch (_: Exception) {
                    "Erreur HTTP ${response.code} : ${response.message}"
                }
                TelegramResult.Error(errorDesc, response.code)
            }
        } catch (e: IOException) {
            TelegramResult.Error("Erreur réseau / connexion : ${e.localizedMessage ?: "Impossible de joindre Telegram"}")
        } catch (e: Exception) {
            TelegramResult.Error("Erreur imprévue : ${e.localizedMessage ?: e.javaClass.simpleName}")
        }
    }

    suspend fun testConnection(botToken: String, chatId: String? = null): TelegramResult = withContext(Dispatchers.IO) {
        val cleanToken = botToken.trim()
        if (cleanToken.isEmpty()) {
            return@withContext TelegramResult.Error("Token manquant")
        }

        val url = "https://api.telegram.org/bot$cleanToken/getMe"
        try {
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val json = JSONObject(body)
                if (json.optBoolean("ok", false)) {
                    val result = json.optJSONObject("result")
                    val botName = result?.optString("first_name", "Bot") ?: "Bot"
                    val username = result?.optString("username", "") ?: ""
                    TelegramResult.Success(
                        rawResponse = "Connecté à @$username ($botName)"
                    )
                } else {
                    val desc = json.optString("description", "Token invalide")
                    TelegramResult.Error(desc)
                }
            } else {
                TelegramResult.Error("Token invalide ou non reconnu (HTTP ${response.code})")
            }
        } catch (e: Exception) {
            TelegramResult.Error("Erreur de connexion : ${e.localizedMessage}")
        }
    }
}
