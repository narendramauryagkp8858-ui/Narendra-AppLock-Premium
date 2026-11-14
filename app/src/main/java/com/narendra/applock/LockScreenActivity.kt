
package com.narendra.applock

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class LockScreenActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "locked_package_name"
    }

    private val dark = Color.rgb(8, 12, 28)
    private val blue = Color.rgb(0, 190, 255)

    private var lockedPackage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lockedPackage = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""

        if (lockedPackage.isBlank()) {
            finish()
            return
        }

        showLockScreen()
    }

    private fun showLockScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28)
            setBackgroundColor(dark)
        }

        val title = TextView(this).apply {
            text = "NARENDRA APPLOCK"
            textSize = 27f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(blue)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "APP IS PROTECTED"
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(12)
        }

        val pinInput = EditText(this).apply {
            hint = "Enter 4-digit MPIN"
            inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD
            filters = arrayOf(InputFilter.LengthFilter(4))
            gravity = Gravity.CENTER
            textSize = 22f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
        }

        val error = TextView(this).apply {
            setTextColor(Color.RED)
            gravity = Gravity.CENTER
            setPadding(8)
        }

        val unlockButton = Button(this).apply {
            text = "UNLOCK APP"
        }

        unlockButton.setOnClickListener {

            val enteredPin = pinInput.text.toString()

            if (PinManager.verifyPin(this, enteredPin)) {

                getSharedPreferences(
                    "narendra_app_lock",
                    MODE_PRIVATE
                ).edit()
                    .putString("session_unlock_package", lockedPackage)
                    .apply()

                finish()

            } else {
                error.text = "Wrong MPIN! Try again."
                pinInput.text.clear()
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(pinInput)
        root.addView(error)
        root.addView(unlockButton)

        setContentView(root)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Back button cannot bypass the app lock.
    }
}
