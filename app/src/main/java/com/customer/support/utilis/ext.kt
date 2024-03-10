package com.customer.support.utilis

import com.customer.support.domain.PrinterModel
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

fun List<PrinterModel>.toMapByModel(): Map<String, PrinterModel> {
    return this.associateBy { it.mPrinterOut }
}

fun String.toBeauty(): String {
    return when (this) {
        "PRINTER_REGISTER" -> "CAJA"
        "PRINTER_PRIMARY" -> "MOSTRADOR"
        "PRINTER_SECONDARY" -> "COCINA"
        "PRINTER_ADDITIONAL" -> "ADICIONAL"
        else -> "UNKNOWN"
    }
}
