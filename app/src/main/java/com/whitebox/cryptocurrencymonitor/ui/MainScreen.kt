package com.whitebox.cryptocurrencymonitor.ui

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.whitebox.cryptocurrencymonitor.ui.assetdetail.AssetDetailScreen
import com.whitebox.cryptocurrencymonitor.ui.assets.AssetScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            AssetScreen(
                onAssetClick = { assetId ->
                    navController.navigate("details/$assetId")
                }
            )
        }

        composable(
            "details/{assetId}",
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId")
            AssetDetailScreen(assetId = assetId ?: "", onBack = { navController.popBackStack() })
        }
    }

}