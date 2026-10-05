package com.fungorn.trainingcapacity.core.common

import kotlin.math.roundToInt

fun formatDecimal(value: Float): String {
    val tenths = (value * 10).roundToInt()
    return "${tenths / 10}.${tenths % 10}"
}

fun formatPercent(ratio: Float): String = "${(ratio * 100).roundToInt()}%"

fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val rest = minutes % 60
    return when {
        hours == 0 -> "$rest min"
        rest == 0 -> "$hours h"
        else -> "$hours h $rest min"
    }
}
