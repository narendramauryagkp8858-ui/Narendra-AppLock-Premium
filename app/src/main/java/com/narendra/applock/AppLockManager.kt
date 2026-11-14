
package com.narendra.applock

import android.content.Context

object AppLockManager {

    private const val PREFS_NAME = "narendra_app_lock"
    private const val LOCKED_APPS_KEY = "locked_apps"

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLockedApps(context: Context): Set<String> {
        return preferences(context)
            .getStringSet(LOCKED_APPS_KEY, emptySet())
            ?.toSet() ?: emptySet()
    }

    fun isAppLocked(context: Context, packageName: String): Boolean {
        return getLockedApps(context).contains(packageName)
    }

    fun lockApp(context: Context, packageName: String) {
        val apps = getLockedApps(context).toMutableSet()
        apps.add(packageName)

        preferences(context).edit()
            .putStringSet(LOCKED_APPS_KEY, apps)
            .apply()
    }

    fun unlockApp(context: Context, packageName: String) {
        val apps = getLockedApps(context).toMutableSet()
        apps.remove(packageName)

        preferences(context).edit()
            .putStringSet(LOCKED_APPS_KEY, apps)
            .apply()
    }

    fun clearAllLocks(context: Context) {
        preferences(context).edit()
            .remove(LOCKED_APPS_KEY)
            .apply()
    }
}
