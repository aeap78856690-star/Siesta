package com.siestaauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    private val fingerprintIdentifier = FingerprintIdentifier(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        fingerprintIdentifier.authenticate(
            onSuccess = { user ->
                // Aquí puedes cargar el usuario
            },
            onError = { error ->
                // Aquí puedes mostrar un error
            }
        )
    }
}
