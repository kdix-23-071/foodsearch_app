package com.example.food_app.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.food_app.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    shopId: String,
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(shopId) {
        viewModel.loadShopDetail(shopId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.shop?.name ?: "詳細") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "エラー: ${uiState.error}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.shop != null -> {
                    ShopDetailContent(shop = uiState.shop!!)
                }
            }
        }
    }
}

@Composable
fun ShopDetailContent(shop: Shop) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = shop.photo.pc.l,
            contentDescription = shop.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailItem(title = "住所", content = shop.address)
            DetailItem(title = "営業時間", content = shop.open)
            DetailItem(title = "アクセス", content = shop.access)
            DetailItem(title = "キャッチコピー", content = shop.catch)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "お店の詳細",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("サンプルサンプルサンプルサンプル")
            Text("・サンプルサンプルサンプルサンプルサンプルサンプルサンプルサンプル")
            Text("・サンプルサンプルサンプルサンプルサンプルサンプルサンプルサンプル")
            Text("・サンプルサンプルサンプルサンプルサンプルサンプルサンプルサンプル")
        }
    }
}

@Composable
fun DetailItem(title: String, content: String) {
    if (content.isNotBlank()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    val dummyShop = Shop(
        id = "1",
        name = "サンプル居酒屋",
        logoImage = "https://imgfp.hotp.jp/IMGH/59/77/P028035977/P028035977_69.jpg",
        nameKana = "サンプルイザカヤ",
        address = "東京都千代田区丸の内1-1-1",
        stationName = "東京",
        lat = 35.681236,
        lng = 139.767125,
        genre = Genre("居酒屋", "説明", "G001"),
        budget = Budget("B001", "3000円", "3000円"),
        catch = "美味しい料理と空間を提供します。宴会にも最適！",
        capacity = 50,
        access = "JR東京駅 八重洲北口 徒歩1分",
        mobileAccess = "東京駅徒歩1分",
        urls = Urls("http://example.com"),
        photo = Photo(
            pc = PcPhoto("https://imgfp.hotp.jp/IMGH/28/83/P034812883/P034812883_238.jpg", "", "https://imgfp.hotp.jp/IMGH/28/83/P034812883/P034812883_100.jpg"),
            mobile = MobilePhoto("https://imgfp.hotp.jp/IMGH/28/83/P034812883/P034812883_168.jpg", "https://imgfp.hotp.jp/IMGH/28/83/P034812883/P034812883_100.jpg")
        ),
        open = "月～金: 17:00～23:00\n土日祝: 12:00～23:00",
        close = "年中無休"
    )

    MaterialTheme {
        Surface {
            ShopDetailContent(shop = dummyShop)
        }
    }
}
