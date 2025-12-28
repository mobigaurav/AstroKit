package com.gaurav.astrokit.platform

import android.content.Intent
import android.net.Uri
import com.gaurav.astrokit.platform.AndroidClipboard.appContext

actual fun openUrl(url: String) {
    val ctx = appContext ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}
