package com.whitebox.cryptocurrencymonitor.ui.assets

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.whitebox.cryptocurrencymonitor.R
import com.whitebox.cryptocurrencymonitor.domain.model.Asset
import com.whitebox.cryptocurrencymonitor.ui.common.ProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetScreen(
    viewModel: AssetsViewModel = hiltViewModel(),
    onAssetClick: (String) -> Unit,
) {
    val assetState by viewModel.assetState.collectAsStateWithLifecycle()
//    val favouriteAssetState by viewModel.favouriteAssetState.collectAsStateWithLifecycle()
//    var isFavourite by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { AppBar() },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar()

            Spacer(modifier = Modifier.height(20.dp))

            AssetList(
                assetState = assetState,
                onAssetClick = onAssetClick
            )
        }

        if (assetState.isLoading) {
            ProgressIndicator()
        }

        val context = LocalContext.current
        LaunchedEffect(assetState.error) {
            if (assetState.error != null) {
                Toast.makeText(context, assetState.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    viewModel: AssetsViewModel = hiltViewModel(),
) {
    val searchBarState by viewModel.searchBarState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = searchBarState.searchString,
            onValueChange = viewModel::onSearchTextChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(18.dp),
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
//                  modifier = Modifier.clickable {  }
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                placeholderColor = Color.Gray
            ),
        )
    }
}

@Composable
fun AssetList(
    assetState: AssetState,
    onAssetClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {

        items(assetState.assets) { item ->
            AssetItem(
                asset = item,
                onAssetClick = onAssetClick,
            )
        }
    }
}

@Composable
fun AssetItem(
    asset: Asset,
    onAssetClick: (String) -> Unit,
    viewModel: AssetsViewModel = hiltViewModel()
) {
    val assetState by viewModel.assetState.collectAsStateWithLifecycle()
    val favouriteAssetState by viewModel.favouriteAssetState.collectAsStateWithLifecycle()
    var isFavourite by remember { mutableStateOf(asset.isFavourite) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .padding(8.dp)
            .background(Color.LightGray)
            .border(
                shape = RoundedCornerShape(8.dp),
                width = 0.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable { onAssetClick(asset.assetId) },
            verticalAlignment = Alignment.CenterVertically,

            ) {
            AsyncImage(
                model = asset.iconUrl,
                modifier = Modifier
                    .padding(0.5.dp)
                    .height(30.dp)
                    .widthIn(max = 30.dp),

                placeholder = painterResource(R.drawable.sharp_money_off_24),
                error = painterResource(id = R.drawable.sharp_money_off_24),
                contentScale = ContentScale.Crop,
                contentDescription = "Icon"
            )

            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    text = asset.assetId
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    color = Color.Gray,
                    text = asset.name
                )
            }

            Spacer(Modifier.weight(1f))
            Log.d("AssetItem", "${asset.assetId} - ${assetState.isFavourite}")

            Icon(
                imageVector =
                if (isFavourite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
//                tint = if (favouriteAssetState.isFavourite) {
//                    Color.Red
//                } else {
//                    Color.Gray
//                },
                contentDescription = "Favourite",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        if (assetState.isFavourite) {
                            viewModel.removeFavouriteAsset(asset.assetId)
                        } else {
                            viewModel.addFavouriteAsset(asset.assetId)
                        }
                        isFavourite = !isFavourite
                    }

            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(
        title = { Text(text = "View Cryptocurrencies", color = Color.White) },
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favourite",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    )
}


@Composable
@Preview
fun PreviewAssetScreen() {
}