package com.whitebox.cryptocurrencymonitor.util

import timber.log.Timber
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Utilities {

    /**
     * Formats a string amount as a currency amount in the format "#,##0.00"
     *
     * This function takes a numeric string representation and attempts to format it as a currency amount
     * using the specified decimal format pattern. If the conversion fails due to an arithmetic or
     * number format exception, an error message is logged and an empty string is returned.
     *
     * @return The formatted currency amount string if successful, otherwise an empty string.
     */
    fun String.formatToCurrencyAmount(): String {
        val formattedAmount: String = try {
            val decimalFormat = DecimalFormat("#,##0.00")
            decimalFormat.format(this.toDouble())
        } catch (e: ArithmeticException) {
            Timber.tag("Utilities").e("formatToCurrencyAmount: %s", e.message)
            ""
        } catch (e: NumberFormatException) {
            Timber.tag("Utilities").e("formatToCurrencyAmount: %s", e.message)
            ""
        }
        return formattedAmount
    }

    /**
     * Converts a string date to a formatted date string
     *
     * This function takes a string representation of a date and attempts to convert it to a formatted
     * date string using the specified date format pattern. If the conversion fails due to a datetime
     * parse exception, an empty string is returned.
     *
     * @return The formatted date string if successful, otherwise an empty string.
     */
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