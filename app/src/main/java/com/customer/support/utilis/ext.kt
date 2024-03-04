package com.customer.support.utilis

import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

fun Long.toFormatDate(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    return dateFormat.format(calendar.time)
}

 fun String?.isUrl(): Boolean {
    return try {
        URL(this).toURI()
        true
    } catch (e: Exception) {
        false
    }
}