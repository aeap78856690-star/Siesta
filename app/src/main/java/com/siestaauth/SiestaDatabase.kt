package com.siestaauth

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ConversationEntity::class], version = 1, exportSchema = false)
abstract class SiestaDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao

    companion object {
        @Volatile
        private var INSTANCE: SiestaDatabase? = null

        fun getDatabase(context: Context): SiestaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiestaDatabase::class.java,
                    "siesta_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
