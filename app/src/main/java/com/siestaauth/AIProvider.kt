package com.siestaauth

interface AIProvider {
    suspend fun chat(message: String): String
    fun getName(): String
}