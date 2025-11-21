package com.example.food_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.food_app.ui.detail.DetailScreen
import com.example.food_app.ui.result.ResultScreen
import com.example.food_app.ui.search.SearchScreen
import com.example.food_app.ui.theme.Food_appTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Food_appTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FoodAppNavigation()
                }
            }
        }
    }
}

@Composable
fun FoodAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        // 検索画面
        composable("search") {
            SearchScreen(navController = navController)
        }

        // 検索結果画面
        composable(
            route = "result/{lat}/{lng}/{range}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType }, // Doubleはサポート外なのでFloatかStringで受け渡すのが無難だが、今回はDoubleをString経由などで渡すか、Floatで妥協するか。通常緯度経度はDouble推奨。
                navArgument("lng") { type = NavType.FloatType },
                navArgument("range") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0
            val range = backStackEntry.arguments?.getInt("range") ?: 3

            ResultScreen(
                lat = lat,
                lng = lng,
                range = range,
                navController = navController
            )
        }

        // 店舗詳細画面
        composable(
            route = "detail/{shopId}",
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            DetailScreen(
                shopId = shopId,
                navController = navController
            )
        }
    }
}
