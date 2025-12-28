package com.gaurav.astrokit.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object AndroidClipboard {
    var appContext: Context? = null
}

actual fun copyToClipboard(label: String, text: String) {
    val ctx = AndroidClipboard.appContext ?: return
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
}
