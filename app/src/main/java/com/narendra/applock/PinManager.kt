
package com.narendra.applock

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinManager {

    private const val PREFS = "narendra_security"
    private const val HASH_KEY = "pin_hash"
    private const val SALT_KEY = "pin_salt"

    private const val DEFAULT_PIN = "8858"

    private fun hashPin(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(
            pin.toCharArray(),
            salt,
            120000,
            256
        )

        return try {
            val factory = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

            Base64.encodeToString(
                factory.generateSecret(spec).encoded,
                Base64.NO_WRAP
            )
        } finally {
            spec.clearPassword()
        }
    }

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        if (!prefs.contains(HASH_KEY)) {
            setPin(context, DEFAULT_PIN)
        }
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            return false
        }

        initialize(context)

        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val savedHash = prefs.getString(HASH_KEY, null)
            ?: return false

        val savedSalt = prefs.getString(SALT_KEY, null)
            ?: return false

        val salt = Base64.decode(savedSalt, Base64.NO_WRAP)
        val enteredHash = hashPin(pin, salt)

        return savedHash == enteredHash
    }

    fun setPin(context: Context, newPin: String): Boolean {
        if (newPin.length != 4 ||
            !newPin.all { it.isDigit() }) {
            return false
        }

        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val hash = hashPin(newPin, salt)

        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        return prefs.edit()
            .putString(HASH_KEY, hash)
            .putString(
                SALT_KEY,
                Base64.encodeToString(salt, Base64.NO_WRAP)
            )
            .commit()
    }
}
