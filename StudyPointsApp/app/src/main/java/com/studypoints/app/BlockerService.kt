package com.studypoints.app

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import org.json.JSONArray

/** Sends the user back to Study Points whenever a blocked app comes to the front while the timer runs. */
class BlockerService : AccessibilityService() {
    private var cachedRaw = ""
    private var cached = setOf<String>()

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        if (e?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = e.packageName?.toString() ?: return
        if (pkg == packageName) return
        val p = getSharedPreferences(FocusBridge.PREFS, Context.MODE_PRIVATE)
        if (!p.getBoolean("on", false)) return
        val raw = p.getString("apps", "[]") ?: "[]"
        if (raw != cachedRaw) {
            cachedRaw = raw
            cached = try {
                val a = JSONArray(raw); (0 until a.length()).map { a.getString(it) }.toSet()
            } catch (ex: Exception) { emptySet() }
        }
        if (pkg in cached) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            packageManager.getLaunchIntentForPackage(packageName)?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                try { startActivity(it) } catch (ex: Exception) {}
            }
            Toast.makeText(this, "Blocked while studying", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInterrupt() {}

    companion object {
        fun isEnabled(ctx: Context): Boolean {
            val me = ComponentName(ctx, BlockerService::class.java).flattenToString()
            val list = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
            return list.split(':').any { it.equals(me, ignoreCase = true) }
        }
    }
}
