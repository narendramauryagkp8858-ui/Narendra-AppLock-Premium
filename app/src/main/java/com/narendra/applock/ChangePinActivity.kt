
package com.narendra.applock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChangePinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_change_pin)

        val oldPin = findViewById<EditText>(R.id.oldPin)
        val newPin = findViewById<EditText>(R.id.newPin)
        val confirmPin = findViewById<EditText>(R.id.confirmPin)

        val message =
            findViewById<TextView>(R.id.changePinMessage)

        val saveButton =
            findViewById<Button>(R.id.saveNewPin)

        saveButton.setOnClickListener {

            val old = oldPin.text.toString()
            val new = newPin.text.toString()
            val confirm = confirmPin.text.toString()

            when {
                !PinManager.verifyPin(this, old) -> {
                    message.text = "Old MPIN is incorrect!"
                }

                new.length != 4 || !new.all { it.isDigit() } -> {
                    message.text = "Enter a valid 4-digit PIN!"
                }

                new == old -> {
                    message.text = "New PIN must be different!"
                }

                new != confirm -> {
                    message.text = "New PIN does not match!"
                }

                PinManager.setPin(this, new) -> {
                    message.setTextColor(
                        android.graphics.Color.GREEN
                    )
                    message.text = "MPIN changed successfully!"

                    oldPin.text.clear()
                    newPin.text.clear()
                    confirmPin.text.clear()
                }

                else -> {
                    message.text = "Unable to save PIN. Try again."
                }
            }
        }
    }
}
