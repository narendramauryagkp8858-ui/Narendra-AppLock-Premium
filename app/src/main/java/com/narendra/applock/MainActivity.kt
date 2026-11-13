
package com.narendra.applock

import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class MainActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(8, 12, 28)
    private val blueColor = Color.rgb(0, 190, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showDashboard()
    }

    private fun showDashboard() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "NARENDRA"
            textSize = 30f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "APPLOCK PREMIUM"
            textSize = 16f
            setTextColor(blueColor)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        addFeature(root, "🔐  APP LOCK") {
            Toast.makeText(
                this,
                "App Lock setup will be added next",
                Toast.LENGTH_SHORT
            ).show()
        }

        addFeature(root, "📷  PHOTO VAULT") {
            Toast.makeText(
                this,
                "Photo Vault setup will be added next",
                Toast.LENGTH_SHORT
            ).show()
        }

        addFeature(root, "🎬  VIDEO VAULT") {
            Toast.makeText(
                this,
                "Video Vault setup will be added next",
                Toast.LENGTH_SHORT
            ).show()
        }

        addFeature(root, "⚙️  SECURITY SETTINGS") {
            Toast.makeText(
                this,
                "Security settings will be added next",
                Toast.LENGTH_SHORT
            ).show()
        }

        setContentView(root)
    }

    private fun addFeature(
        root: LinearLayout,
        label: String,
        action: () -> Unit
    ) {

        val button = TextView(this).apply {
            text = label
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(20)
            setBackgroundColor(Color.rgb(20, 42, 75))
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            65
        )

        params.setMargins(0, 18, 0, 0)

        root.addView(button, params)
    }
}
