package com.siestaauth

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat

class FingerPrintIdentifier(private val context: Context) {

    fun authenticateUser(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess("u001")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError("Error: $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Huella no reconocida")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación de Siesta")
            .setSubtitle("Usa tu huella digital para acceder")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
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
