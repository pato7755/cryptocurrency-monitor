package com.whitebox.cryptocurrencymonitor.ui.assets

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    val context = LocalContext.current
    var visible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { AppBar({ visible = !visible }) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(visible = visible)

            Spacer(modifier = Modifier.height(20.dp))

            AssetList(
                assetState = assetState,
                onAssetClick = onAssetClick
            )
        }

        // show progress indicator when loading
        if (assetState.isLoading) {
            ProgressIndicator()
        }

        // show error message if there is an error
        LaunchedEffect(assetState.error) {
            if (assetState.error != null) {
                Toast.makeText(context, assetState.error, Toast.LENGTH_LONG).show()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onTabBarSearchClicked: () -> Unit,
    viewModel: AssetsViewModel = hiltViewModel(),
) {
    var isTopBarFavouriteSelected by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.view_cryptocurrencies),
                color = Color.White
            )
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(
                onClick = {
                    isTopBarFavouriteSelected = !isTopBarFavouriteSelected
                    if (isTopBarFavouriteSelected)
                        viewModel.getFavouriteAssets() // fetch favourite assets
                    else
                        viewModel.getAssetsAndIcons() // fetch all assets
                },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = if (isTopBarFavouriteSelected) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.favourite),
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { onTabBarSearchClicked() },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = Color.White
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    visible: Boolean,
    viewModel: AssetsViewModel = hiltViewModel(),
) {
    val searchBarState by viewModel.searchBarState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            TextField(
                value = searchBarState.searchString,
                onValueChange = viewModel::onSearchTextChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.search)) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search),
                        modifier = Modifier.size(18.dp),
                    )
                },
                trailingIcon = {
                    if (searchBarState.searchString.isNotEmpty()) {
                        // show close button when search bar text is not empty
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = Color.Black,
                            modifier = Modifier.clickable {
                                // reset search bar text and fetch all assets
                                viewModel.onSearchTextChanged("")
                                viewModel.getAssetsAndIcons(fetchFromRemote = false)
                            }
                        )
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    placeholderColor = Color.Gray
                )
            )
        }
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
    var isFavourite by rememberSaveable(asset.assetId) { mutableStateOf(asset.isFavourite) }

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
                contentDescription = stringResource(R.string.icon)
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

            Icon(
                imageVector =
                if (isFavourite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = stringResource(id = R.string.favourite),
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        isFavourite = !isFavourite
                        if (isFavourite) {
                            viewModel.addFavouriteAsset(asset.assetId)
                        } else {
                            viewModel.removeFavouriteAsset(asset.assetId)
                        }
                    }
            )
        }
    }

}

@Composable
@Preview
fun PreviewAssetScreen() {
}