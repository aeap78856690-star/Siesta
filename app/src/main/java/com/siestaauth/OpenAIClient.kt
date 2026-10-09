package com.siestaauth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class OpenAIClient(private val apiKey: String) : AIProvider {

    override suspend fun chat(message: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.openai.com/v1/chat/completions")

            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $apiKey")

            val requestBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONObject().apply {
                    put("role", "user")
                    put("content", message)
                })
            }

            connection.outputStream.write(requestBody.toString().toByteArray())

            val response = connection.inputStream.bufferedReader().readText()
            val jsonResponse = JSONObject(response)

            val text = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            return@withContext text
        } catch (e: Exception) {
            return@withContext "Error: ${e.message}"
        }
    }

    override fun getName(): String = "OpenAI"
}