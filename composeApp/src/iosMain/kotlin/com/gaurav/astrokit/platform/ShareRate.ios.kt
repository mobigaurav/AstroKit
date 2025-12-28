package com.gaurav.astrokit.platform

import platform.Foundation.NSURL
import platform.UIKit.*

actual fun shareText(subject: String, text: String) {
    val controller = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
    val activity = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
    controller.presentViewController(activity, animated = true, completion = null)
}

actual fun rateApp() {
    // TODO: replace with your App Store ID later: itms-apps://apps.apple.com/app/idYOUR_ID?action=write-review
    val url = NSURL(string = "https://apps.apple.com") ?: return
    UIApplication.sharedApplication.openURL(url)
}
