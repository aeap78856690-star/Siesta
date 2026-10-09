package com.siestaauth

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.content.SharedPreferences

class BatteryOptimizer(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "siesta_battery",
        Context.MODE_PRIVATE
    )

    // Obtener nivel de batería actual
    fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    }

    // Obtener porcentaje de batería
    fun getBatteryPercentage(): Int {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level > 0 && scale > 0) (level * 100) / scale else -1
    }

    // Obtener temperatura del dispositivo
    fun getDeviceTemperature(): Int {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)
        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
    }

    // Obtener estado de carga
    fun isCharging(): Boolean {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    // Modo económico automático
    fun enableEconomicMode(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            prefs.edit().putBoolean("economic_mode", true).apply()
            return true
        }
        return false
    }

    fun disableEconomicMode() {
        prefs.edit().putBoolean("economic_mode", false).apply()
    }

    fun isEconomicModeEnabled(): Boolean {
        return prefs.getBoolean("economic_mode", false)
    }

    // Activar modo reposo después de X minutos sin uso
    fun setIdleTimeout(minutes: Int) {
        prefs.edit().putInt("idle_timeout", minutes).apply()
    }

    fun getIdleTimeout(): Int {
        return prefs.getInt("idle_timeout", 5) // 5 minutos por defecto
    }

    // Reducir brillo de pantalla para ahorrar batería
    fun setScreenBrightness(brightness: Float) {
        prefs.edit().putFloat("screen_brightness", brightness).apply()
    }

    fun getScreenBrightness(): Float {
        return prefs.getFloat("screen_brightness", 0.7f)
    }

    // Control de temperatura
    fun isDeviceOverheated(): Boolean {
        val temp = getDeviceTemperature()
        return temp > 45 // Más de 45°C es "caliente"
    }

    fun getTemperatureStatus(): String {
        val temp = getDeviceTemperature()
        return when {
            temp < 0 -> "Desconocida"
            temp < 30 -> "Fría ❄️"
            temp < 35 -> "Normal 🟢"
            temp < 40 -> "Cálida 🟡"
            temp < 45 -> "Muy cálida 🔴"
            else -> "¡SOBRECALENTADA! ⚠️"
        }
    }

    // Pausar IA si la temperatura es muy alta
    fun shouldPauseAI(): Boolean {
        return isDeviceOverheated()
    }

    // Reducir frecuencia de actualización si batería baja
    fun getUpdateFrequency(): Long {
        val batteryPercent = getBatteryPercentage()
        return when {
            batteryPercent > 50 -> 1000 // 1 segundo
            batteryPercent > 20 -> 3000 // 3 segundos
            else -> 5000 // 5 segundos (batería muy baja)
        }
    }

    // Obtener recomendaciones
    fun getOptimizationTips(): String {
        val tips = mutableListOf<String>()

        if (getBatteryPercentage() < 20) {
            tips.add("⚡ Batería baja: considera usar modo offline")
        }

        if (isDeviceOverheated()) {
            tips.add("🌡️ Dispositivo muy caliente: descansa la app")
        }

        if (!isCharging()) {
            tips.add("🔌 Conecta a cargador para mejor rendimiento")
        }

        return tips.joinToString("\n")
    }
}