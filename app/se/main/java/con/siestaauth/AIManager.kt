package com.siestaauth

class AIManager {

    private var currentProvider: AIProvider? = null

    fun setGemini(apiKey: String) {
        currentProvider = GeminiClient(apiKey)
    }

    fun setOpenAI(apiKey: String) {
        currentProvider = OpenAIClient(apiKey)
    }

    suspend fun chat(message: String): String {
        return currentProvider?.chat(message) ?: "Proveedor no configurado"
    }

    fun getCurrentProvider(): String {
        return currentProvider?.getName() ?: "Ninguno"
    }
}
