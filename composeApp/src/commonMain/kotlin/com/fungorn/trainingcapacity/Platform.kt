package com.fungorn.trainingcapacity

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform