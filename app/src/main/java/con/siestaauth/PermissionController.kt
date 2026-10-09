package com.siestaauth

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log

data class UserProfile(
    val userId: String,
    val userName: String,
    val relationshipToOwner: String,
    val permissions: UserPermissions
)

data class UserPermissions(
    var canChat: Boolean = false,
    var canUseVoice: Boolean = false,
    var canAccessSiesta: Boolean = false,
    var canAccessXender: Boolean = false,
    var canAccessMemory: Boolean = false,
    var canViewMemory: Boolean = false,
    var canDeleteMemory: Boolean = false,
    var canAccessCamera: Boolean = false,
    var canAccessGallery: Boolean = false,
    var canChangeLanguage: Boolean = false,
    var canModifySettings: Boolean = false,
    var canManageUsers: Boolean = false,
    var aiAccessLevel: String = "none"
)

class PermissionController(private val context: Context) {

    fun getPermissionsForUser(currentUser: UserProfile): UserPermissions {
        return when (currentUser.relationshipToOwner) {
            "owner" -> UserPermissions(
                canChat = true,
                canUseVoice = true,
                canAccessSiesta = true,
                canAccessXender = true,
                canAccessMemory = true,
                canViewMemory = true,
                canDeleteMemory = true,
                canAccessCamera = true,
                canAccessGallery = true,
                canChangeLanguage = true,
                canModifySettings = true,
                canManageUsers = true,
                aiAccessLevel = "full"
            )
            "novia" -> UserPermissions(
                canChat = true,
                canUseVoice = true,
                canAccessSiesta = true,
                canAccessXender = true,
                canAccessMemory = true,
                canViewMemory = true,
                canDeleteMemory = true,
                canAccessCamera = true,
                canAccessGallery = true,
                canChangeLanguage = true,
                canModifySettings = true,
                canManageUsers = false,
                aiAccessLevel = "full"
            )
            "prima" -> UserPermissions(
                canChat = false,
                canUseVoice = false,
                canAccessSiesta = false,
                canAccessXender = false,
                canAccessMemory = false,
                canViewMemory = false,
                canDeleteMemory = false,
                canAccessCamera = true,
                canAccessGallery = true,
                canChangeLanguage = true,
                canModifySettings = true,
                canManageUsers = false,
                aiAccessLevel = "none"
            )
            else -> UserPermissions()
        }
    }

    fun canAccessFeature(currentUser: UserProfile, feature: String): Boolean {
        val permissions = getPermissionsForUser(currentUser)

        return when (feature) {
            "siesta_ai" -> permissions.canAccessSiesta
            "xender" -> permissions.canAccessXender
            "memory" -> permissions.canAccessMemory
            "camera" -> permissions.canAccessCamera
            "gallery" -> permissions.canAccessGallery
            "chat" -> permissions.canChat
            "voice" -> permissions.canUseVoice
            "settings" -> permissions.canModifySettings
            "language" -> permissions.canChangeLanguage
            else -> false
        }
    }

    fun blockUnauthorizedAccess(currentUser: UserProfile, feature: String, context: Context) {
        if (!canAccessFeature(currentUser, feature)) {
            showBlockMessage(context)
            logUnauthorizedAttempt(currentUser, feature)
            exitFeature(context)
        }
    }

    private fun showBlockMessage(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Acceso denegado")
            .setMessage("No tienes acceso a esta función.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }

    private fun exitFeature(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    private fun logUnauthorizedAttempt(currentUser: UserProfile, feature: String) {
        val log = "Usuario: ${currentUser.userName} intentó acceder sin permiso a la función: $feature"
        Log.w("SiestaSecurity", log)
    }
}
