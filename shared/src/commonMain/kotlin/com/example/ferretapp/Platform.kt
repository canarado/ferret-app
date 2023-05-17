package com.example.ferretapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform