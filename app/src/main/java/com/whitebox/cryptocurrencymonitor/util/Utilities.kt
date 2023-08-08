package com.whitebox.cryptocurrencymonitor.util

import android.util.Log
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Utilities {

    fun String.formatToCurrencyAmount(): String {
        val formattedAmount: String = try {
            val decimalFormat = DecimalFormat("#,##0.00")
            decimalFormat.format(this.toDouble())
        } catch (e: ArithmeticException) {
            Log.e("Utilities", "formatToCurrencyAmount: ${e.message}")
            ""
        } catch (e: NumberFormatException) {
            Log.e("Utilities", "formatToCurrencyAmount: ${e.message}")
            ""
        }
        return formattedAmount
    }

    fun String.convertDate(): String {
        val result: String
        try {
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX")
            val parsedDate = LocalDateTime.parse(this, dateFormat)
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            result = parsedDate.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            return ""
        }
        return result
    }

}