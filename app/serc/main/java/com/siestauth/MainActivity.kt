package com.siesta.ai

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val title = TextView(this).apply {
            text = "Siesta AI"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        layout.addView(title)
        setContentView(layout)
    }
}