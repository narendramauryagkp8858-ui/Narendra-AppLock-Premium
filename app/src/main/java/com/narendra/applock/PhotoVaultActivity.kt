
package com.narendra.applock

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoVaultActivity : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var photoList: LinearLayout

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            saveSelectedPhoto(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo_vault)

        status = findViewById(R.id.photoVaultStatus)
        photoList = findViewById(R.id.photoList)

        val addButton = findViewById<Button>(
            R.id.addPhotoButton
        )

        addButton.setOnClickListener {
            photoPicker.launch(arrayOf("image/*"))
        }

        refreshPhotos()
    }

    private fun saveSelectedPhoto(uri: android.net.Uri) {

        status.text = "Encrypting photo securely..."

        lifecycleScope.launch {

            val result = withContext(Dispatchers.IO) {
                runCatching {
                    VaultCrypto.encryptPhoto(
                        this@PhotoVaultActivity,
                        uri
                    )
                }
            }

            result.onSuccess {
                status.text = "Photo saved securely!"
                Toast.makeText(
                    this@PhotoVaultActivity,
                    "Photo added to Private Vault",
                    Toast.LENGTH_LONG
                ).show()

                refreshPhotos()
            }

            result.onFailure { error ->
                status.text = "Unable to save photo"
                Toast.makeText(
                    this@PhotoVaultActivity,
                    error.message ?: "Photo encryption failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun refreshPhotos() {

        lifecycleScope.launch {

            val photos = withContext(Dispatchers.IO) {
                VaultCrypto.getPhotos(this@PhotoVaultActivity)
            }

            photoList.removeAllViews()

            status.text = "Secure photos: ${photos.size}"

            if (photos.isEmpty()) {
                val empty = TextView(this@PhotoVaultActivity).apply {
                    text = "No private photos yet"
                    textSize = 16f
                    setTextColor(
                        android.graphics.Color.WHITE
                    )
                    setPadding(20, 30, 20, 30)
                }

                photoList.addView(empty)
            } else {
                photos.forEachIndexed { index, file ->

                    val item = TextView(this@PhotoVaultActivity).apply {
                        text = "🔒  Private Photo ${index + 1}"
                        textSize = 16f
                        setTextColor(
                            android.graphics.Color.WHITE
                        )
                        setPadding(20, 20, 20, 20)
                    }

                    photoList.addView(item)
                }
            }
        }
    }
}
