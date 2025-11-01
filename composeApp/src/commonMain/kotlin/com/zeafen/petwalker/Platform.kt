package com.zeafen.petwalker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform