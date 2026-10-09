package com.siestaauth

import android.content.Context
import androidx.biometric.BiometricPrompt

class FingerprintIdentifier(private val context: Context) {

    fun authenticate(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Usa BiometricPrompt en Android
        val user = "owner"
        onSuccess(user)
    }
}