
package com.narendra.applock

import android.content.Context
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object VaultCrypto {

    private const val KEY_ALIAS = "NarendraVaultKey"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 128

    private fun getSecretKey(): SecretKey {

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val existing = keyStore.getKey(KEY_ALIAS, null)

        if (existing is SecretKey) {
            return existing
        }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val specification = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()

        generator.init(specification)

        return generator.generateKey()
    }

    fun encryptPhoto(
        context: Context,
        uri: Uri
    ): File {

        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open selected photo")

        val directory = File(context.filesDir, "private_photos")

        if (!directory.exists() && !directory.mkdirs()) {
            input.close()
            throw IllegalStateException("Cannot create vault directory")
        }

        val outputFile = File(
            directory,
            "${java.util.UUID.randomUUID()}.nvault"
        )

        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

            val iv = cipher.iv

            FileOutputStream(outputFile).use { fileOutput ->
                fileOutput.write(iv)

                CipherOutputStream(
                    BufferedOutputStream(fileOutput),
                    cipher
                ).use { encryptedOutput ->

                    input.use { photoInput ->
                        photoInput.copyTo(encryptedOutput)
                    }
                }
            }

            return outputFile

        } catch (error: Exception) {
            outputFile.delete()
            input.close()
            throw error
        }
    }

    fun decryptPhoto(
        context: Context,
        encryptedFile: File
    ): File {

        val cacheDirectory = File(
            context.cacheDir,
            "vault_preview"
        )

        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs()
        }

        val outputFile = File(
            cacheDirectory,
            "${java.util.UUID.randomUUID()}.jpg"
        )

        try {
            DataInputStream(
                BufferedInputStream(
                    FileInputStream(encryptedFile)
                )
            ).use { input ->

                val iv = ByteArray(IV_SIZE)
                input.readFully(iv)

                val cipher = Cipher.getInstance(TRANSFORMATION)

                cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(),
                    GCMParameterSpec(TAG_SIZE, iv)
                )

                CipherInputStream(input, cipher).use { decrypted ->
                    FileOutputStream(outputFile).use { output ->
                        decrypted.copyTo(output)
                    }
                }
            }

            return outputFile

        } catch (error: Exception) {
            outputFile.delete()
            throw error
        }
    }

    fun getPhotos(context: Context): List<File> {

        val directory = File(context.filesDir, "private_photos")

        return directory.listFiles()
            ?.filter { it.isFile && it.extension == "nvault" }
            ?: emptyList()
    }

    fun deletePhoto(file: File): Boolean {
        return file.delete()
    }
}
