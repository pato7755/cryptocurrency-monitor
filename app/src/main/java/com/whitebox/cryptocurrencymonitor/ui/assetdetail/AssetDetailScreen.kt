package com.whitebox.cryptocurrencymonitor.ui.assetdetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whitebox.cryptocurrencymonitor.R
import com.whitebox.cryptocurrencymonitor.common.Constants
import com.whitebox.cryptocurrencymonitor.ui.common.ProgressIndicator

@Composable
@ExperimentalMaterial3Api
fun AssetDetailScreen(
    assetId: String,
    onBack: () -> Unit,
    viewModel: AssetDetailsViewModel = hiltViewModel(),
) {
    val assetDetailState by viewModel.assetDetailsState.collectAsStateWithLifecycle()
    val exchangeRateState by viewModel.exchangeRateState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Scaffold(
        topBar = { AppBar(onBack = onBack) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            assetDetailState.asset?.let { assetDetail ->
                // Asset details
                Text(
                    text = stringResource(id = R.string.currency) + " : ${assetDetail.assetId}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${stringResource(id = R.string.name)} : ${assetDetail.name}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${stringResource(R.string.is_cryptocurrency)} " +
                            if (assetDetail.typeIsCrypto == 1) Constants.YesOrNo.YES.name
                            else Constants.YesOrNo.NO.name,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.is_favourite) +
                            " ${assetDetail.isFavourite}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.usd_price) +
                            " ${viewModel.formatAmount(assetDetail.priceUsd)}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(30.dp))

                // Asset's EUR exchange rate
                exchangeRateState.exchangeRate?.let { exchangeRate ->
                    Text(
                        text = stringResource(R.string.exchange_rate),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.one) +
                                " ${exchangeRate.assetIdQuote} " +
                                stringResource(id = R.string.equals) +
                                " ${viewModel.formatAmount(exchangeRate.rate.toString())} " +
                                exchangeRate.assetIdBase,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.as_at) + " ${
                            viewModel.convertDate(
                                exchangeRate.time
                            )
                        }",
                        fontSize = 14.sp
                    )
                }
            }

        }
    }
    // show progress indicator when loading
    if (assetDetailState.isLoading || exchangeRateState.isLoading) {
        ProgressIndicator()
    }
    // show error message if there is an error while fetching asset details
    LaunchedEffect(assetDetailState.error) {
        if (assetDetailState.error != null) {
            Toast.makeText(context, assetDetailState.error, Toast.LENGTH_LONG).show()
        }
    }
    // show error message if there is an error while fetching exchange rate
    LaunchedEffect(exchangeRateState.error) {
        if (exchangeRateState.error != null) {
            Toast.makeText(context, exchangeRateState.error, Toast.LENGTH_LONG).show()
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AppBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.details), color = Color.White) },
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
        }
    )
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