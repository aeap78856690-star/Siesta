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