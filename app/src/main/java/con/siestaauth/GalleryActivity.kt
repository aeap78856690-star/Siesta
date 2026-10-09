package com.siestaauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Usuario actual de prueba
        val currentUser = UserProfile(
            userId = "u0003",
            userName = "Prima",
            relationshipToOwner = "prima",
            permissions = UserPermissions()
        )

        val permissionController = PermissionController(this)

        // Verificación de permisos obligatoria para acceder a la galería
        if (!permissionController.canAccessFeature(currentUser, "gallery")) {
            permissionController.blockUnauthorizedAccess(currentUser, "gallery", this)
            finish()
            return
        }

        // Aquí se abriría la galería real
    }
}
