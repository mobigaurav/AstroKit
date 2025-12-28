package com.gaurav.astrokit.platform

import android.content.pm.PackageManager
import com.gaurav.astrokit.platform.AndroidClipboard.appContext

actual fun getAppInfo(): AppInfo {
    val ctx = appContext
    if (ctx == null) return AppInfo(versionName = "unknown", buildNumber = "unknown")

    return try {
        val pm = ctx.packageManager
        val pkg = ctx.packageName
        val pi = pm.getPackageInfo(pkg, 0)

        val versionName = pi.versionName ?: "unknown"
        val buildNumber = if (android.os.Build.VERSION.SDK_INT >= 28) {
            pi.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            pi.versionCode.toString()
        }

        AppInfo(versionName = versionName, buildNumber = buildNumber)
    } catch (_: PackageManager.NameNotFoundException) {
        AppInfo(versionName = "unknown", buildNumber = "unknown")
    }
}
