package com.utp.mediconecta.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val iso = DateTimeFormatter.ISO_LOCAL_DATE
    private val pretty = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "PA"))

    fun pretty(isoDate: String): String = runCatching {
        LocalDate.parse(isoDate, iso).format(pretty)
    }.getOrDefault(isoDate)

    fun day(isoDate: String): String = runCatching {
        LocalDate.parse(isoDate).dayOfMonth.toString().padStart(2, '0')
    }.getOrDefault("--")

    fun month(isoDate: String): String = runCatching {
        LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("MMM", Locale("es", "PA"))).replace(".", "")
    }.getOrDefault("---")

    fun isUpcoming(isoDate: String): Boolean = runCatching {
        !LocalDate.parse(isoDate).isBefore(LocalDate.now())
    }.getOrDefault(false)
}
