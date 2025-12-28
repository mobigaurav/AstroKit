package com.gaurav.astrokit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform