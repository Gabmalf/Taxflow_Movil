package com.gmaldonado.taxflow_movil.data.formatters

import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {
    private val localePe = Locale.forLanguageTag("es-PE")

    fun centimosToPen(centimos: Long): Double {
        return centimos / 100.0
    }

    fun formatCentimosAsPen(centimos: Long): String {
        val pen = centimos / 100.0
        return String.format(localePe, "S/ %,.2f", pen)
    }

    fun formatPen(amount: Double): String {
        return String.format(localePe, "S/ %,.2f", amount)
    }

    fun formatBasisPoints(pb: Int): String {
        return "${pb / 100}%"
    }

    fun formatIsoDate(isoDate: String): String {
        return try {
            val date = LocalDate.parse(isoDate)
            val monthNames = arrayOf(
                "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
            )
            "${date.dayOfMonth} ${monthNames[date.monthValue - 1]} ${date.year}"
        } catch (_: Exception) {
            isoDate
        }
    }

    fun formatIsoDateTime(isoDateTime: String): String {
        return try {
            val zdt = ZonedDateTime.parse(isoDateTime)
            val monthNames = arrayOf(
                "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
            )
            val timeStr = String.format(localePe, "%02d:%02d", zdt.hour, zdt.minute)
            "${zdt.dayOfMonth} ${monthNames[zdt.monthValue - 1]} ${zdt.year} • $timeStr"
        } catch (_: Exception) {
            isoDateTime
        }
    }
}
