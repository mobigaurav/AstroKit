package com.gaurav.astrokit.platform

data class AppInfo(
    val versionName: String,
    val buildNumber: String
)

expect fun getAppInfo(): AppInfo
