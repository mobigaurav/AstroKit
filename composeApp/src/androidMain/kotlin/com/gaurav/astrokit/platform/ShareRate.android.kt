package com.gaurav.astrokit.platform

import android.content.Intent
import android.net.Uri
import com.gaurav.astrokit.platform.AndroidClipboard.appContext

actual fun shareText(subject: String, text: String) {
    val ctx = appContext ?: return
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(Intent.createChooser(intent, "Share").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

actual fun rateApp() {
    val ctx = appContext ?: return
    // TODO: replace with your real package name later (same as android applicationId)
    val pkg = ctx.packageName
    val uri = Uri.parse("market://details?id=$pkg")
    val web = Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    try {
        ctx.startActivity(intent)
    } catch (_: Throwable) {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, web).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
    }
}
