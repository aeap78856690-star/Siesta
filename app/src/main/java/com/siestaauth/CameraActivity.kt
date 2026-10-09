package com.siestaauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Usuario actual de prueba
        val currentUser = UserProfile(
            userId = "u003",
            userName = "Prima",
            relationshipToOwner = "prima",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        // Verificación de permisos obligatoria para acceder a la cámara
        if (!permissionController.canAccessFeature(currentUser, "camera")) {
            permissionController.blockUnauthorizedAccess(currentUser, "camera", this)
            finish()
            return
        }

        // Aquí se abriría la cámara real
    }
}
