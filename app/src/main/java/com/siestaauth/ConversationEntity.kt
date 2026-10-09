package com.siestaauth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val role: String, // "user" o "assistant"
    val content: String,
    val timestamp: Long,
    val language: String = "es"
)
