package com.siestaauth

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConversationDao {

    @Insert
    suspend fun insertMessage(message: ConversationEntity)

    @Query("SELECT * FROM conversations WHERE userId = :userId")
    suspend fun getConversationByUser(userId: String): List<ConversationEntity>

    @Query("DELETE FROM conversations WHERE userId = :userId")
    suspend fun clearConversation(userId: String)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteMessage(id: Int)

    @Query("SELECT COUNT(*) FROM conversations WHERE userId = :userId")
    suspend fun getConversationCount(userId: String): Int

    @Query("SELECT * FROM conversations WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getConversationByDateRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): List<ConversationEntity>
}
