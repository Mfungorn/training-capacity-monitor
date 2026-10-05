package com.fungorn.trainingcapacity.core.common

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun formatMillisToDate(milliseconds: Long): String {
    val instant = Instant.fromEpochMilliseconds(milliseconds)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatted = "${localDateTime.day}/${localDateTime.month.number}"
    return formatted
}

fun formatMillisToIsoDate(milliseconds: Long): String {
    val localDate = Instant.fromEpochMilliseconds(milliseconds)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    return localDate.toString()
}
