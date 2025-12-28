package com.gaurav.astrokit.core

import com.russhwolf.settings.Settings

object AppDI {
    val settings: Settings by lazy {
        Settings()
    }
}
