package com.siestaauth

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat

class BiometricVoiceAuthenticator(private val context: Context) {

    private lateinit var prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences("siesta_prefs", Context.MODE_PRIVATE)
    }

    fun verifyBiometricForVoice(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        // Verificar que el usuario está registrado
        val userRegistered = prefs.getBoolean("first_time", true)

        if (userRegistered) {
            onError("Usuario no registrado")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    
                    val userName = prefs.getString("user_name", "Unknown") ?: "Unknown"
                    val userRelationship = prefs.getString("user_relationship", "owner") ?: "owner"
                    
                    onSuccess("$userName:$userRelationship")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError("Autenticación fallida: $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Huella no reconocida")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verificar identidad")
            .setSubtitle("Usa tu huella para activar Siesta")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}