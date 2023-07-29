package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whitebox.cryptocurrencymonitor.common.Constants

@Composable
@ExperimentalMaterial3Api
fun AssetDetailScreen(
    assetId: String,
    onBack: () -> Unit,
    viewModel: AssetDetailsViewModel = hiltViewModel(),
) {
    val assetDetailState by viewModel.assetDetailsState.collectAsStateWithLifecycle()
    val exchangeRateState by viewModel.exchangeRateState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details",
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            assetDetailState.asset?.let { assetDetail ->
                // Asset details
                Text(
                    text = "Currency : ${assetDetail.assetId}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Name : ${assetDetail.name}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Is Cryptocurrency : " +
                            if (assetDetail.typeIsCrypto == 1) Constants.YesOrNo.YES.name
                            else Constants.YesOrNo.NO.name,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Is Favourite : ${assetDetail.isFavourite}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "USD price : $${assetDetail.priceUsd}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(30.dp))

                // Asset's EUR exchange rate
                exchangeRateState.exchangeRate?.let { exchangeRate ->
                    Text(
                        text = "Exchange Rate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1 ${exchangeRate.assetIdQuote} = " +
                                "${exchangeRate.rate} ${exchangeRate.assetIdBase}",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "As at ${exchangeRate.time}",
                        fontSize = 14.sp
                    )
                }
            }

        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PreviewAssetDetailsScreen() {
    AssetDetailScreen(
        assetId = "",
        onBack = {}
    )
}