package com.siestaauth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setup)

        val prefs = getSharedPreferences("siesta_prefs", Context.MODE_PRIVATE)

        val btnYo = findViewById<Button>(R.id.btnYo)
        val btnNovia = findViewById<Button>(R.id.btnNovia)
        val btnPrima = findViewById<Button>(R.id.btnPrima)

        btnYo.setOnClickListener {
            saveUserAndGoAuth("owner", "Yo", prefs)
        }

        btnNovia.setOnClickListener {
            saveUserAndGoAuth("novia", "Novia", prefs)
        }

        btnPrima.setOnClickListener {
            saveUserAndGoAuth("prima", "Prima", prefs)
        }
    }

    private fun saveUserAndGoAuth(relationship: String, userName: String, prefs: android.content.SharedPreferences) {
        prefs.edit().apply {
            putString("user_relationship", relationship)
            putString("user_name", userName)
            putBoolean("first_time", false)
            apply()
        }

        Toast.makeText(this, "Registrando usuario...", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}

