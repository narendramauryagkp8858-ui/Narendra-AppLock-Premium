
package com.narendra.applock

import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class MainActivity : AppCompatActivity() {

    private val dark = Color.rgb(8, 12, 28)
    private val blue = Color.rgb(0, 190, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PinManager.initialize(this)
        showPinScreen()
    }

    private fun showPinScreen() {

        setContentView(R.layout.activity_pin)

        val pinInput = findViewById<EditText>(R.id.pinInput)
        val pinError = findViewById<TextView>(R.id.pinError)
        val verify = findViewById<Button>(R.id.verifyPin)

        verify.setOnClickListener {

            val pin = pinInput.text.toString()

            if (PinManager.verifyPin(this, pin)) {
                showDashboard()
            } else {
                pinError.text = "Incorrect MPIN! Try again."
                pinInput.text.clear()
            }
        }
    }

    private fun showDashboard() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24)
            setBackgroundColor(dark)
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
            setTextColor(blue)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        addFeature(root, "🔐  APP LOCK") {
            Toast.makeText(
                this, "App Lock module coming next",
                Toast.LENGTH_SHORT
            ).show()
        }

        addFeature(root, "📷  PHOTO VAULT") {
            Toast.makeText(
                this, "Photo Vault module coming next",
                Toast.LENGTH_SHORT
            ).show()
        }

        addFeature(root, "🎬  VIDEO VAULT") {
            Toast.makeText(
                this, "Video Vault module coming next",
                Toast.LENGTH_SHORT
            ).show()
        }

        addFeature(root, "⚙️  SECURITY SETTINGS") {
            Toast.makeText(
                this, "Security settings coming next",
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
