package com.siestaauth

import android.content.Context

class FingerprintIdentifier(private val context: Context) {

    fun authenticateUser(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Base de ejemplo.
        // Aquí se integraría BiometricPrompt.
        val user = "owner"
        onSuccess(user)
    }
}
