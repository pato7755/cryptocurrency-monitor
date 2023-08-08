package com.whitebox.cryptocurrencymonitor.util

import com.google.common.truth.Truth.assertThat
import com.whitebox.cryptocurrencymonitor.util.Utilities.convertDate
import com.whitebox.cryptocurrencymonitor.util.Utilities.formatToCurrencyAmount
import org.junit.Test

internal class UtilitiesTest {

    @Test
    fun `formatToCurrencyAmount should return formatted amount with 2 decimal places`() {
        val amount = 1234567.8473
        val formattedAmount = amount.toString().formatToCurrencyAmount()
        assertThat("1,234,567.85").isEqualTo(formattedAmount)
    }

    @Test
    fun `convertDate should return valid date in String format`() {
        val date = "2023-08-08T06:48:46.0000000Z"
        val formattedDate = date.convertDate()
        assertThat("08.08.2023 06:48:46").isEqualTo(formattedDate)
    }

    @Test
    fun `convertDate should return empty string when date cannot be parsed`() {
        val date = "2023"
        val formattedDate = date.convertDate()
        assertThat("").isEqualTo(formattedDate)
    }

}
