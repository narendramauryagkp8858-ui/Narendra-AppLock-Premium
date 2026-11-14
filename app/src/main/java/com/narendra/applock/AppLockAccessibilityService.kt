
package com.narendra.applock

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockAccessibilityService : AccessibilityService() {

    private var lastObservedPackage: String? = null
    private var promptedPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        if (
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) return

        val packageName = event.packageName?.toString() ?: return

        // Ignore Narendra AppLock itself
        if (packageName == this.packageName) return

        // Reset prompt state when the user changes apps
        if (packageName != lastObservedPackage) {
            lastObservedPackage = packageName
            promptedPackage = null
        }

        val prefs = getSharedPreferences(
            "narendra_app_lock",
            MODE_PRIVATE
        )

        val unlockedPackage = prefs.getString(
            "session_unlock_package",
            null
        )

        // Clear temporary unlock when user leaves that app
        if (unlockedPackage != null && unlockedPackage != packageName) {
            prefs.edit()
                .remove("session_unlock_package")
                .apply()
        }

        // Allow the app if it was unlocked with the correct MPIN
        if (unlockedPackage == packageName) return

        // Check whether this app is protected
        if (!AppLockManager.isAppLocked(this, packageName)) {
            promptedPackage = null
            return
        }

        // Avoid repeatedly opening multiple lock screens
        if (promptedPackage == packageName) return

        promptedPackage = packageName

        val lockIntent = Intent(
            this,
            LockScreenActivity::class.java
        ).apply {
            putExtra(
                LockScreenActivity.EXTRA_PACKAGE_NAME,
                packageName
            )

            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

        startActivity(lockIntent)
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
    }
}
