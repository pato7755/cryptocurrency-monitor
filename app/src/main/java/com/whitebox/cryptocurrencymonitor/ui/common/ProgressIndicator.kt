package com.whitebox.cryptocurrencymonitor.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.whitebox.cryptocurrencymonitor.ui.theme.CryptocurrencyMonitorTheme

@Composable
fun ProgressIndicator() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp),
            color = Color.Blue,
            strokeWidth = Dp(value = 2F)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIndicator() {
    CryptocurrencyMonitorTheme() {
        ProgressIndicator()
    }
}
