package com.siestaauth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class GeminiClient(private val apiKey: String) : AIProvider {

    override suspend fun chat(message: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            )

            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")

            val requestBody = JSONObject().apply {
                put("contents", JSONObject().apply {
                    put("parts", JSONObject().apply {
                        put("text", message)
                    })
                })
            }

            connection.outputStream.write(requestBody.toString().toByteArray())

            val response = connection.inputStream.bufferedReader().readText()
            val jsonResponse = JSONObject(response)

            val text = jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            return@withContext text
        } catch (e: Exception) {
            return@withContext "Error: ${e.message}"
        }
    }

    override fun getName(): String = "Gemini"
}