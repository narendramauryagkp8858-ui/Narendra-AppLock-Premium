
package com.narendra.applock

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

        val pinInput = findViewById<android.widget.EditText>(R.id.pinInput)
        val pinError = findViewById<TextView>(R.id.pinError)
        val verify = findViewById<android.widget.Button>(R.id.verifyPin)

        verify.setOnClickListener {
            if (PinManager.verifyPin(this, pinInput.text.toString())) {
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
            setPadding(20)
            setBackgroundColor(dark)
        }

        val title = TextView(this).apply {
            text = "NARENDRA"
            textSize = 32f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "APPLOCK PREMIUM 3D"
            textSize = 16f
            setTextColor(blue)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        addFeature(root, "APP LOCK") {
            Toast.makeText(this, "App Lock module is not ready yet", Toast.LENGTH_SHORT).show()
        }

        addFeature(root, "PHOTO VAULT") {
            startActivity(Intent(this, PhotoVaultActivity::class.java))
        }

        addFeature(root, "VIDEO VAULT") {
            Toast.makeText(this, "Video Vault module is coming next", Toast.LENGTH_SHORT).show()
        }

        addFeature(root, "CHANGE MPIN") {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }

        addFeature(root, "SECURITY SETTINGS") {
            Toast.makeText(this, "Security settings are coming next", Toast.LENGTH_SHORT).show()
        }

        setContentView(root)
    }

    private fun addFeature(
        root: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = TextView(this).apply {
            text = "✦  $label"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(12, 0, 12, 0)
            setBackgroundColor(Color.rgb(20, 42, 75))
            isClickable = true
            isFocusable = true
            minHeight = 64
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            64
        )
        params.setMargins(0, 10, 0, 0)
        root.addView(button, params)
    }
}
