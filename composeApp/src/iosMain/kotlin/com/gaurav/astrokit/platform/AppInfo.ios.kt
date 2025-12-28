package com.gaurav.astrokit.platform

import platform.Foundation.NSBundle

actual fun getAppInfo(): AppInfo {
    val bundle = NSBundle.mainBundle

    val versionName = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "unknown"
    val buildNumber = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "unknown"

    return AppInfo(versionName = versionName, buildNumber = buildNumber)
}
