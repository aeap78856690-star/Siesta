package com.siesta.app

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GeminiClient(private val apiKey: String) {

    private val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        return@withContext try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            // Construcción del cuerpo de la petición en formato JSON
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            // Envío de la petición
            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            // Lectura de la respuesta
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                parseResponse(responseText)
            } else {
                "Error en la petición: ${connection.responseCode} ${connection.responseMessage}"
            }
        } catch (e: Exception) {
            "Excepción al conectar con Gemini: ${e.localizedMessage}"
        } finally {
            connection.disconnect()
        }
    }

    private fun parseResponse(jsonResponse: String): String {
        return try {
            val json = JSONObject(jsonResponse)
            val candidates = json.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val firstPart = parts.getJSONObject(0)
            firstPart.getString("text")
        } catch (e: Exception) {
            "Error al procesar la respuesta de Gemini."
        }
    }
}
