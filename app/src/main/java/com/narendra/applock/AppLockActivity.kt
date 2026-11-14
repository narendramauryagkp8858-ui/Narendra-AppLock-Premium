
package com.narendra.applock

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class AppLockActivity : AppCompatActivity() {

    private val dark = 0xFF080C1C.toInt()
    private val blue = 0xFF00BEFF.toInt()
    private lateinit var appList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showScreen()
    }

    override fun onResume() {
        super.onResume()
        if (::appList.isInitialized) loadApps()
    }

    private fun hasUsageAccess(): Boolean {
        val ops = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = ops.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun showScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16)
            setBackgroundColor(dark)
        }

        val title = TextView(this).apply {
            text = "NARENDRA APP LOCK"
            textSize = 25f
            setTextColor(blue)
            gravity = Gravity.CENTER
            setPadding(12)
        }

        val permissionButton = Button(this).apply {
            text = "ENABLE USAGE ACCESS"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }

        val info = TextView(this).apply {
            text = "Select apps you want to protect. Usage Access is needed for app monitoring."
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(8)
        }

        appList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            addView(appList)
        }

        root.addView(title)
        root.addView(permissionButton)
        root.addView(info)
        root.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(root)
        loadApps()
    }

    private fun loadApps() {
        appList.removeAllViews()

        if (!hasUsageAccess()) {
            val message = TextView(this).apply {
                text = "Please enable Usage Access above, then return here."
                setTextColor(0xFFFFCC66.toInt())
                textSize = 16f
                setPadding(12)
            }
            appList.addView(message)
        }

        val pm = packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = pm.queryIntentActivities(
            launcherIntent,
            PackageManager.MATCH_ALL
        ).map { it.activityInfo.applicationInfo }
            .distinctBy { it.packageName }
            .filter { it.packageName != packageName }
            .sortedBy {
                pm.getApplicationLabel(it).toString().lowercase()
            }

        if (apps.isEmpty()) {
            val empty = TextView(this).apply {
                text = "No launchable apps found."
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(12)
            }
            appList.addView(empty)
            return
        }

        apps.forEach { app ->
            addAppRow(app, pm)
        }
    }

    private fun addAppRow(app: ApplicationInfo, pm: PackageManager) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12)
            setBackgroundColor(0xFF142A4B.toInt())
        }

        val name = TextView(this).apply {
            text = pm.getApplicationLabel(app).toString()
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val toggle = Button(this).apply {
            text = if (AppLockManager.isAppLocked(this@AppLockActivity, app.packageName))
                "LOCKED" else "LOCK"

            setOnClickListener {
                if (AppLockManager.isAppLocked(this@AppLockActivity, app.packageName)) {
                    AppLockManager.unlockApp(this@AppLockActivity, app.packageName)
                    text = "LOCK"
                    Toast.makeText(this@AppLockActivity, "App unlocked", Toast.LENGTH_SHORT).show()
                } else {
                    AppLockManager.lockApp(this@AppLockActivity, app.packageName)
                    text = "LOCKED"
                    Toast.makeText(this@AppLockActivity, "App added to lock list", Toast.LENGTH_SHORT).show()
                }
            }
        }

        row.addView(name)
        row.addView(toggle)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 6, 0, 6)
        appList.addView(row, params)
    }
}
