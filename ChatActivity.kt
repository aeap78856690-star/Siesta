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
package com.siestaauth

import android.app.AlertDialog
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
    private lateinit var smartAISelector: SmartAISelector
    private lateinit var voiceListener: BiometricVoiceListener
    private lateinit var biometricAuthenticator: BiometricAuthenticator
    private lateinit var conversationDB: ConversationDatabase
    private lateinit var languageManager: LanguageManager
    private lateinit var networkManager: NetworkManager

    private var voiceSpeed = 1.0f
    private var voicePitch = 1.2f
    private var isListeningForKeyword = false

    private val GEMINI_API_KEY = "TU_API_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Recuperar el perfil del usuario desde el Intent
        val currentUser = UserProfile(
            userId = intent.getStringExtra("user_id") ?: "u001",
            userName = intent.getStringExtra("user_name") ?: "Yo",
            relationshipToOwner = intent.getStringExtra("user_relationship") ?: "owner",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        // 2. Validación obligatoria de permisos de acceso al chat
        if (!permissionController.canAccessFeature(currentUser, "chat")) {
            permissionController.blockUnauthorizedAccess(currentUser, "chat", this)
            finish()
            return
        }

        // 3. Inicializar componentes básicos en orden sucesivo
        mikoVoice = MikoNakanoVoice(this)
        conversationDB = ConversationDatabase(this)
        languageManager = LanguageManager(this)
        networkManager = NetworkManager(this)

        // 4. Configurar el selector de Inteligencia Artificial
        val geminiClient = GeminiClient(GEMINI_API_KEY)
        val localAIModel = LocalAIModel(this)
        smartAISelector = SmartAISelector(this, geminiClient, localAIModel, networkManager)

        // 5. Inicializar sistemas de seguridad y escucha de voz pasiva
        biometricAuthenticator = BiometricAuthenticator(this)
        voiceListener = BiometricVoiceListener(this) {
            onSiestaKeywordDetected()
        }

        // 6. Vinculación de vistas de la interfaz de usuario (UI)
        val chatOutput = findViewById<TextView>(R.id.chatOutput)
        val chatInput = findViewById<EditText>(R.id.chatInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val speakButton = findViewById<Button>(R.id.speakButton)
        val listenKeywordButton = findViewById<Button>(R.id.listenKeywordButton)
        val memoryButton = findViewById<Button>(R.id.memoryButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val languageButton = findViewById<Button>(R.id.languageButton)

        val speedSeekBar = findViewById<SeekBar>(R.id.speedSeekBar)
        val pitchSeekBar = findViewById<SeekBar>(R.id.pitchSeekBar)

        // Estado inicial de la conexión en pantalla
        updateStatusMessage(chatOutput)

        // 7. Listeners para los SeekBars (Velocidad y Tono)
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceSpeed = (progress / 50.0f).coerceIn(0.5f, 2.0f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        pitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voicePitch = (progress / 50.0f).coerceIn(0.5f, 2.0f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 8. Lógica del Botón de Escucha de Palabra Clave
        listenKeywordButton.setOnClickListener {
            if (!isListeningForKeyword) {
                chatOutput.text = "🎙️ Escuchando palabra clave..."
                voiceListener.startListening()
                isListeningForKeyword = true
                listenKeywordButton.text = "Detener Escucha"
            } else {
                voiceListener.stopListening()
                isListeningForKeyword = false
                listenKeywordButton.text = "Escuchar Palabra Clave"
                updateStatusMessage(chatOutput)
            }
        }

        // 9. Lógica del Botón Enviar (Guardando historial en base de datos de manera sucesiva)
        sendButton.setOnClickListener {
            val text = chatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                chatOutput.text = "Tú: $text"
                conversationDB.saveMessage(currentUser.userId, Message("user", text))

                lifecycleScope.launch {
                    val response = smartAISelector.selectAndChat(text)
                    chatOutput.text = "IA: $response"
                    conversationDB.saveMessage(currentUser.userId, Message("assistant", response))
                    updateStatusMessage(chatOutput)
                }
                chatInput.setText("")
            }
        }

        // 10. Lógica del Botón Hablar (Procesamiento de Voz Asistida)
        speakButton.setOnClickListener {
            val text = chatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                chatOutput.text = "🎙️ Hablando..."
                conversationDB.saveMessage(currentUser.userId, Message("user", text))

                lifecycleScope.launch {
                    val response = smartAISelector.selectAndChat(text)
                    chatOutput.text = "IA: $response"
                    conversationDB.saveMessage(currentUser.userId, Message("assistant", response))
                    mikoVoice.speak(response, voiceSpeed, voicePitch)
                    updateStatusMessage(chatOutput)
                }
                chatInput.setText("")
            }
        }

        // 11. Listeners para Diálogos de Ajustes Auxiliares
        memoryButton.setOnClickListener {
            showMemoryDialog(currentUser.userId)
        }

        languageButton.setOnClickListener {
            showLanguageDialog()
        }

        settingsButton.setOnClickListener {
            showSettingsDialog(chatOutput)
        }
    }

    private fun updateStatusMessage(chatOutput: TextView) {
        val status = smartAISelector.getCurrentStatus()
        val connectionType = networkManager.getConnectionType()
        chatOutput.text = "$status ($connectionType)"
    }

    private fun showMemoryDialog(userId: String) {
        val conversation = conversationDB.getConversation(userId)
        if (conversation.isEmpty()) {
            Toast.makeText(this, "No hay memoria guardada", Toast.LENGTH_SHORT).show()
            return
        }

        val items = conversation.map { "${it.role}: ${it.content}" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Memoria de conversación")
            .setItems(items) { _, which ->
                showMessageDetails(conversation[which])
            }
            .setNeutralButton("Borrar todo") { _, _ ->
                conversationDB.clearConversation(userId)
                Toast.makeText(this, "Memoria limpia", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showMessageDetails(message: Message) {
        AlertDialog.Builder(this)
            .setTitle(message.role)
            .setMessage(message.content)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLanguageDialog() {
        val languages = languageManager.getAvailableLanguages()
        val displayNames = languages.map { languageManager.getLanguageName(it) }.toTypedArray()
        val currentLang = languageManager.getCurrentLanguage()
        val selected = languages.indexOf(currentLang)

        AlertDialog.Builder(this)
            .setTitle("Cambiar idioma")
            .setSingleChoiceItems(displayNames, selected) { dialog, which ->
                val code = languages[which]
                languageManager.setLanguage(code)
                mikoVoice.onInit(0) // Fuerza refresco del idioma en Text-to-Speech
                Toast.makeText(this, "Idioma cambiado a: ${displayNames[which]}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showSettingsDialog(chatOutput: TextView) {
        val modes = arrayOf("Automático", "Online Forzado", "Offline Forzado")
        val currentMode = when (smartAISelector.getAIMode()) {
            "auto" -> 0
            "online" -> 1
            "offline" -> 2
            else -> 0
        }

        AlertDialog.Builder(this)
            .setTitle("Modo de IA")
            .setSingleChoiceItems(modes, currentMode) { dialog, which ->
                val targetMode = when (which) {
                    0 -> "auto"
                    1 -> "online"
                    2 -> "offline"
                    else -> "auto"
                }
                smartAISelector.setAIMode(targetMode)
                updateStatusMessage(chatOutput)
                Toast.makeText(this, "Modo de IA cambiado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

