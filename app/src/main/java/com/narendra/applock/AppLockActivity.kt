
package com.narendra.applock

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class AppLockActivity : AppCompatActivity() {

    private val dark = Color.rgb(8, 12, 28)
    private val blue = Color.rgb(0, 190, 255)

    private lateinit var serviceStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showScreen()
    }

    override fun onResume() {
        super.onResume()
        if (::serviceStatus.isInitialized) {
            updateServiceStatus()
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val manager = getSystemService(
            ACCESSIBILITY_SERVICE
        ) as AccessibilityManager

        return manager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        ).any { info ->
            val service = info.resolveInfo.serviceInfo
            service.packageName == packageName &&
                service.name == AppLockAccessibilityService::class.java.name
        }
    }

    private fun updateServiceStatus() {
        if (isAccessibilityEnabled()) {
            serviceStatus.text = "● APP LOCK SERVICE: ENABLED"
            serviceStatus.setTextColor(Color.GREEN)
        } else {
            serviceStatus.text = "● APP LOCK SERVICE: DISABLED"
            serviceStatus.setTextColor(Color.RED)
        }
    }

    private fun showScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20)
            setBackgroundColor(dark)
        }

        val title = TextView(this).apply {
            text = "NARENDRA APP LOCK"
            textSize = 25f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(blue)
            gravity = Gravity.CENTER
            setPadding(12)
        }

        serviceStatus = TextView(this).apply {
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(16)
        }

        val enableButton = Button(this).apply {
            text = "ENABLE ACCESSIBILITY SERVICE"
            setOnClickListener {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
        }

        val instruction = TextView(this).apply {
            text = "Settings में Narendra AppLock Service खोलें और उसे ON करें।"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(10)
        }

        val listTitle = TextView(this).apply {
            text = "SELECT APPS TO LOCK"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setPadding(12)
        }

        root.addView(title)
        root.addView(serviceStatus)
        root.addView(enableButton)
        root.addView(instruction)
        root.addView(listTitle)

        val scroll = ScrollView(this)
        val appList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(appList)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
        updateServiceStatus()
        loadInstalledApps(appList)
    }

    private fun loadInstalledApps(container: LinearLayout) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager.queryIntentActivities(
            intent,
            0
        ).filter {
            it.activityInfo.packageName != packageName
        }.distinctBy {
            it.activityInfo.packageName
        }.sortedBy {
            it.loadLabel(packageManager).toString().lowercase()
        }

        for (app in apps) {
            val appPackage = app.activityInfo.packageName
            val appName = app.loadLabel(packageManager).toString()

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(10)
                setBackgroundColor(Color.rgb(20, 42, 75))
            }

            val name = TextView(this).apply {
                text = appName
                textSize = 15f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val lockButton = Button(this).apply {
                text = if (
                    AppLockManager.isAppLocked(
                        this@AppLockActivity,
                        appPackage
                    )
                ) "LOCKED" else "LOCK"

                setOnClickListener {
                    if (
                        AppLockManager.isAppLocked(
                            this@AppLockActivity,
                            appPackage
                        )
                    ) {
                        AppLockManager.unlockApp(
                            this@AppLockActivity,
                            appPackage
                        )
                        text = "LOCK"
                    } else {
                        AppLockManager.lockApp(
                            this@AppLockActivity,
                            appPackage
                        )
                        text = "LOCKED"
                    }

                    Toast.makeText(
                        this@AppLockActivity,
                        "$appName updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            row.addView(name)
            row.addView(lockButton)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 5, 0, 5)

            container.addView(row, params)
        }
    }
}
