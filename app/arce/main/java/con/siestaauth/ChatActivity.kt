package com.siestaauth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Usuario actual de prueba
        val currentUser = UserProfile(
            userId = "u001",
            userName = "Yo",
            relationshipToOwner = "owner",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        // Verificación de permisos obligatoria para acceder al chat
        if (!permissionController.canAccessFeature(currentUser, "chat")) {
            permissionController.blockUnauthorizedAccess(currentUser, "chat", this)
            finish()
            return
        }

        // Inicialización de las vistas de la interfaz
        val chatOutput = findViewById<TextView>(R.id.chatOutput)
        val chatInput = findViewById<EditText>(R.id.chatInput)
        val sendButton = findViewById<Button>(R.id.sendButton)

        chatOutput.text = "Siesta está lista."

        // Acción del botón para enviar mensajes
        sendButton.setOnClickListener {
            val text = chatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                chatOutput.text = "Tu mensaje: $text"
                chatInput.setText("")
            }
        }
    }
}
