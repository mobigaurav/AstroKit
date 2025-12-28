package com.gaurav.astrokit.platform

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun nowEpochMillis(): Long {
    val seconds = NSDate().timeIntervalSince1970
    return (seconds * 1000.0).toLong()
}
