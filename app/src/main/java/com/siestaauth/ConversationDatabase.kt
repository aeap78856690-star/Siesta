package com.siestaauth

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class Message(
    val role: String, // "user" o "assistant"
    val content: String,
    val timestamp: Long
)

class ConversationDatabase(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "siesta_conversations",
        Context.MODE_PRIVATE
    )

    fun saveMessage(userId: String, message: Message) {
        val key = "conversation_$userId"
        val existingJson = prefs.getString(key, "[]") ?: "[]"
        
        val jsonArray = JSONArray(existingJson)
        
        val messageJson = JSONObject().apply {
            put("role", message.role)
            put("content", message.content)
            put("timestamp", message.timestamp)
        }
        
        jsonArray.put(messageJson)
        prefs.edit().putString(key, jsonArray.toString()).apply()
    }

    fun getConversation(userId: String): List<Message> {
        val key = "conversation_$userId"
        val json = prefs.getString(key, "[]") ?: "[]"
        
        val messages = mutableListOf<Message>()
        val jsonArray = JSONArray(json)
        
        for (i in 0 until jsonArray.length()) {
            val messageJson = jsonArray.getJSONObject(i)
            messages.add(
                Message(
                    role = messageJson.getString("role"),
                    content = messageJson.getString("content"),
                    timestamp = messageJson.getLong("timestamp")
                )
            )
        }
        
        return messages
    }

    fun clearConversation(userId: String) {
        val key = "conversation_$userId"
        prefs.edit().remove(key).apply()
    }

    fun deleteMessage(userId: String, index: Int) {
        val key = "conversation_$userId"
        val existingJson = prefs.getString(key, "[]") ?: "[]"
        
        val jsonArray = JSONArray(existingJson)
        
        // Crear nuevo array sin el mensaje en el índice
        val newArray = JSONArray()
        for (i in 0 until jsonArray.length()) {
            if (i != index) {
                newArray.put(jsonArray.getJSONObject(i))
            }
        }
        
        prefs.edit().putString(key, newArray.toString()).apply()
    }

    fun getConversationSize(userId: String): Int {
        val key = "conversation_$userId"
        val json = prefs.getString(key, "[]") ?: "[]"
        return JSONArray(json).length()
    }
}