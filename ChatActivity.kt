package com.siestaauth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChatActivity : AppCompatActivity() {

    private lateinit var mikoVoice: MikoNakanoVoice
    private var voiceSpeed = 1.0f
    private var voicePitch = 1.2f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val currentUser = UserProfile(
            userId = intent.getStringExtra("user_id") ?: "u001",
            userName = intent.getStringExtra("user_name") ?: "Yo",
            relationshipToOwner = intent.getStringExtra("user_relationship") ?: "owner",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        if (!permissionController.canAccessFeature(currentUser, "siesta_ai")) {
            permissionController.blockUnauthorizedAccess(currentUser, "siesta_ai", this)
            finish()
            return
        }

        mikoVoice = MikoNakanoVoice(this)

        val chatOutput = findViewById<TextView>(R.id.chatOutput)
        val chatInput = findViewById<EditText>(R.id.chatInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val speakButton = findViewById<Button>(R.id.speakButton)

        val speedSeekBar = findViewById<SeekBar>(R.id.speedSeekBar)
        val pitchSeekBar = findViewById<SeekBar>(R.id.pitchSeekBar)

        chatOutput.text = "Siesta está activa. ¿En qué te puedo ayudar?"

        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceSpeed = (progress / 50f).coerceIn(0.5f, 2.0f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        pitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voicePitch = (progress / 50f).coerceIn(0.5f, 2.0f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sendButton.setOnClickListener {
            val text = chatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                chatOutput.text = "Tu: $text\n\nSiesta: Aquí estoy."
                chatInput.setText("")
            }
        }

        speakButton.setOnClickListener {
            val text = chatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                chatOutput.text = "Hablaste: $text\n\nSiesta habla..."
                mikoVoice.speak(text, voiceSpeed, voicePitch)
                chatInput.setText("")
            } else {
                Toast.makeText(this, "Escribe algo primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        mikoVoice.shutdown()
        super.onDestroy()
    }
}
package com.siestaauth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var mikoVoice: MikoNakanoVoice
    private lateinit var aiManager: AIManager
    private var voiceSpeed = 1.0f
    private var voicePitch = 1.2f

    private val GEMINI_API_KEY = "TU_API_KEY_AQUI"
    private val OPENAI_API_KEY = "TU_API_KEY_AQUI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Recuperar el perfil del usuario desde el Intent de origen
        val currentUser = UserProfile(
            userId = intent.getStringExtra("user_id") ?: "u001",
            userName = intent.getStringExtra("user_name") ?: "Yo",
            relationshipToOwner = intent.getStringExtra("user_relationship") ?: "owner",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        // Validación estricta de permisos de acceso al chat
        if (!permissionController.canAccessFeature(currentUser, "chat")) {
            permissionController.blockUnauthorizedAccess(currentUser, "chat", this)
            finish()
            return
        }

        // Inicialización de componentes internos
        mikoVoice = MikoNakanoVoice(this)
        aiManager = AIManager()
        aiManager.setGemini(GEMINI_API_KEY)

        // Vinculación de vistas de la interfaz de usuario
        val chatOutput = findViewById<TextView>(R.id.chatOutput)
        val chatInput = findViewById<EditText>(R.id.chatInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val speakButton = findViewById<Button>(R.id.speakButton)
        val switchAIButton = findViewById<Button>(R.id.switchAIButton)

        val speedSeekBar = findViewById<SeekBar>(R.id.speedSeekBar)
        val pitchSeekBar = findViewById<SeekBar>(R.id.pitchSeekBar)

        chatOutput.text = "Siesta activa."

        // Configuración del control deslizante para la velocidad de la voz
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceSpeed = (progress / 50.0f).coerceIn(0.5f, 2.0f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configuración del control deslizante para el tono de la voz
        pitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voicePitch = (progress / 50.0f).coerceIn(0.5f, 2.0f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Acción para alternar entre proveedores de Inteligencia Artificial (Gemini / OpenAI)
        switchAIButton.setOnClickListener {
            val currentAI = aiManager.getCurrentProvider()
            if (currentAI == "Gemini") {
                aiManager.setOpenAI(OPENAI_API_KEY)
                chatOutput.text = "IA cambiada a OpenAI"
                Toast.makeText(this, "AI Provider: OpenAI", Toast.LENGTH_SHORT).show()
            } else {
                aiManager.setGemini(GEMINI_API_KEY)
                chatOutput.text = "IA cambiada a Gemini"
                Toast.makeText(this, "AI Provider: Gemini", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción para enviar mensajes en modo texto tradicional
        sendButton.setOnClickListener {
            val text = chatInput.text.toString().trim()

            if (text.isNotEmpty()) {
                chatOutput.text = "Tú: $text"

                lifecycleScope.launch {
                    val response = aiManager.chat(text)
                    chatOutput.text = "IA: $response"
                }

                chatInput.setText("")
            } else {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción para enviar mensajes usando respuesta asistida por voz (Text-to-Speech)
        speakButton.setOnClickListener {
            val text = chatInput.text.toString().trim()

            if (text.isNotEmpty()) {
                chatOutput.text = "Hablando..."

                lifecycleScope.launch {
                    val response = aiManager.chat(text)
                    chatOutput.text = "IA: $response"
                    mikoVoice.speak(response, voiceSpeed, voicePitch)
                }

                chatInput.setText("")
            } else {
                Toast.makeText(this, "Escribe algo para hablar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        mikoVoice.shutdown()
        super.onDestroy()
    }
}
package com.siestaauth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var mikoVoice: MikoNakanoVoice
    private lateinit var geminiClient: GeminiClient
    private lateinit var voiceListener: BiometricVoiceListener
    private lateinit var biometricAuthenticator: BiometricAuthenticator
    
    private var voiceSpeed = 1.0f
    private var voicePitch = 1.2f
    private var isListeningForKeyword = false

    private val GEMINI_API_KEY = "TU_API_KEY_AQUI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Recuperar el perfil del usuario desde el Intent
        val currentUser = UserProfile(
            userId = intent.getStringExtra("user_id") ?: "u001",
            userName = intent.getStringExtra("user_name") ?: "Yo",
            relationshipToOwner = intent.getStringExtra("user_relationship") ?: "owner",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        // Validación estricta de permisos de acceso al chat
        if (!permissionController.canAccessFeature(currentUser, "chat")) {
            permissionController.blockUnauthorizedAccess(currentUser, "chat", this)
            finish()
            return
        }

        // Inicialización de componentes internos
        mikoVoice = MikoNakanoVoice(this)
        geminiClient = GeminiClient(GEMINI_API_KEY)
        biometricAuthenticator = BiometricAuthenticator(this)

        // Inicialización del escuchador de voz para la palabra clave "Siesta"
        voiceListener = BiometricVoiceListener(this) {
            // Cuando detecta "Siesta"
            onSiestaKeywordDetected()
        }

        // Vinculación de vistas de la interfaz de usuario
        val chatOutput = findViewById<TextView>(R.id.chatOutput)
        val chatInput = findViewById<EditText>(R.id.chatInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val speakButton = findViewById<Button>(R.id.speakButton)
        val listenKeywordButton = findViewById<Button>(R.id.listenKeywordButton)

        val speedSeekBar = findViewById<SeekBar>(R.id.speedSeekBar)
        val pitchSeekBar = findViewById<SeekBar>(R.id.pitchSeekBar)

        chatOutput.text = "Siesta activa."

        // Configuración del control de velocidad de la voz
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceSpeed = (progress / 50.0f).coerceIn(0.5f, 2.0f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configuración del control de tono de la voz
        pitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voicePitch = (progress / 50.0f).coerceIn(0.5f, 2.0f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Botón para activar/desactivar la escucha pasiva de la palabra clave
        listenKeywordButton.setOnClickListener {
            if (!isListeningForKeyword) {
                chatOutput.text = "Escuchando palabra clave..."
                voiceListener.startListening()
                isListeningForKeyword = true
                listenKeywordButton.text = "Detener Escucha"
            } else {
                voiceListener.stopListening()
                isListeningForKeyword = false
                listenKeywordButton.text = "Escuchar Palabra Clave"
                chatOutput.text = "Escucha detenida."
            }
        }

        // Acción para enviar mensajes en modo texto tradicional
        sendButton.setOnClickListener {
            val text = chatInput.text.toString().trim()

            if (text.isNotEmpty()) {
                chatOutput.text = "Tú: $text"

                lifecycleScope.launch {
                    val response = geminiClient.chat(text)
                    chatOutput.text = "IA: $response"
                }

                chatInput.setText("")
            }
        }

        // Acción para enviar mensajes usando respuesta asistida por voz (Text-to-Speech)
        speakButton.setOnClickListener {
            val text = chatInput.text.toString().trim()

            if (text.isNotEmpty()) {
                chatOutput.text = "Hablando..."

                lifecycleScope.launch {
                    val response = geminiClient.chat(text)
                    chatOutput.text = "IA: $response"
                    mikoVoice.speak(response, voiceSpeed, voicePitch)
                }

                chatInput.setText("")
            } else {
                Toast.makeText(this, "Escribe algo para hablar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Ejecución lógica cuando la palabra clave "Siesta" es reconocida en segundo plano
    private fun onSiestaKeywordDetected() {
        // Verificar huella digital de manera automática
        biometricAuthenticator.verifyBiometrics(
            onSuccess = { userInfo ->
                val chatOutput = findViewById<TextView>(R.id.chatOutput)
                chatOutput.text = "Siesta: Acceso concedido por voz"
                Toast.makeText(this, "Identidad Verificada", Toast.LENGTH_SHORT).show()

                // Aquí podrías iniciar acciones guiadas por voz
                // Por ahora solo confirma el acceso seguro
            },
            onError = { error ->
                val chatOutput = findViewById<TextView>(R.id.chatOutput)
                chatOutput.text = error
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroy() {
        voiceListener.stopListening()
        mikoVoice.shutdown()
        super.onDestroy()
    }
}

