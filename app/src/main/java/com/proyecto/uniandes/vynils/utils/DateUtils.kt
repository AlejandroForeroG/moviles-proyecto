package com.proyecto.uniandes.vynils.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.toIso8601UtcFromDdMMyyyy(): String {
    // If already looks like ISO 8601 UTC, return as is
    val isoRegex = Regex("^\\d{4}-\\d{2}-\\d{2}T.*Z$")
    if (this.matches(isoRegex)) return this

    return try {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Interpret the input date as UTC midnight to produce a stable ISO UTC output
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(this)

        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")
        outputFormat.format(date!!)
    } catch (e: Exception) {
        // If parsing fails, return the original string so caller can handle validation
        this
    }
}
