package com.siestaauth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var permissionController: PermissionController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Usuario actual de prueba
        val currentUser = UserProfile(
            userId = "u0001",
            userName = "Yo",
            relationshipToOwner = "owner",
            permissions = UserPermissions()
        )

        permissionController = PermissionController(this)

        val btnAuth = findViewById<Button>(R.id.btnAuth)
        val btnSiesta = findViewById<Button>(R.id.btnSiesta)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)

        btnAuth.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        btnSiesta.setOnClickListener {
            handleFeatureAccess(currentUser, "siesta_ai", ChatActivity::class.java)
        }

        btnCamera.setOnClickListener {
            handleFeatureAccess(currentUser, "camera", CameraActivity::class.java)
        }

        btnGallery.setOnClickListener {
            handleFeatureAccess(currentUser, "gallery", GalleryActivity::class.java)
        }
    }

    private fun handleFeatureAccess(currentUser: UserProfile, feature: String, targetActivity: Class<*>) {
        if (permissionController.canAccessFeature(currentUser, feature)) {
            startActivity(Intent(this, targetActivity))
        } else {
            permissionController.blockUnauthorizedAccess(currentUser, feature, this)
        }
    }
}