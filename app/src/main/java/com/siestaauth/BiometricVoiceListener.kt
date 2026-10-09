package com.siestaauth

import android.content.Context
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast

class BiometricVoiceListener(
    private val context: Context,
    private val onSiestaDetected: () -> Unit
) : RecognitionListener {

    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false

    fun startListeningForKeyword() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Reconocimiento de voz no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(this)
        isListening = true

        val intent = android.speech.RecognizerIntent.getLanguageDetailsIntent(context)
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        if (::speechRecognizer.isInitialized && isListening) {
            speechRecognizer.stopListening()
            isListening = false
        }
    }

    override fun onReadyForSpeech(params: android.os.Bundle?) {
        Log.d("VoiceListener", "Escuchando palabra clave...")
    }

    override fun onBeginningOfSpeech() {
        Log.d("VoiceListener", "Detectado sonido")
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {}

    override fun onError(error: Int) {
        Log.e("VoiceListener", "Error: $error")
        // Reintentar escuchar
        if (isListening) {
            startListeningForKeyword()
        }
    }

    override fun onResults(results: android.os.Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        if (matches != null && matches.isNotEmpty()) {
            val spokenText = matches[0].lowercase()

            Log.d("VoiceListener", "Escuchado: $spokenText")

            // Detectar la palabra clave "Siesta"
            if (spokenText.contains("siesta")) {
                Log.d("VoiceListener", "¡PALABRA CLAVE DETECTADA!")
                onSiestaDetected()
            }
        }

        // Reiniciar escucha
        if (isListening) {
            startListeningForKeyword()
        }
    }

    override fun onPartialResults(partialResults: android.os.Bundle?) {}

    override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
}