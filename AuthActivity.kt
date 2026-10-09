package com.siestaauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    private val fingerprintIdentifier = FingerprintIdentifier(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fingerprintIdentifier.authenticate(
            onSuccess = { user ->
                // Cargar usuario
                // abrir la app
            },
            onError = { error ->
                // mostrar error
            }
        )
    }
}

