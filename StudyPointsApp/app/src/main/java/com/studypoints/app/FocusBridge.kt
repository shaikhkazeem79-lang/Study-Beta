package com.studypoints.app

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONObject

/** Exposed to the web app as window.AndroidFocus */
class FocusBridge(private val ctx: Context) {

    /** Every app with a launcher icon on THIS phone: [{"n":"Free Fire","p":"com.dts.freefireth"}, ...] */
    @JavascriptInterface
    fun getInstalledApps(): String {
        val pm = ctx.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val arr = JSONArray()
        val seen = HashSet<String>()
        for (ri in pm.queryIntentActivities(intent, 0)) {
            val pkg = ri.activityInfo.packageName
            if (pkg == ctx.packageName || !seen.add(pkg)) continue
            arr.put(JSONObject().put("n", ri.loadLabel(pm).toString()).put("p", pkg))
        }
        return arr.toString()
    }

    /** on = timer running. appsJson = ["com.dts.freefireth", ...]. Saved, so it survives app restarts. */
    @JavascriptInterface
    fun setFocus(on: Boolean, appsJson: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean("on", on).putString("apps", appsJson).apply()
    }

    @JavascriptInterface
    fun hasPermission(): Boolean = BlockerService.isEnabled(ctx)

    /** Opens Accessibility settings where the user turns on "Study Points". */
    @JavascriptInterface
    fun openSettings() {
        ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @JavascriptInterface
    fun startVideo(id: String) { /* video plays inside the web page */ }

    companion object { const val PREFS = "focus" }
}
