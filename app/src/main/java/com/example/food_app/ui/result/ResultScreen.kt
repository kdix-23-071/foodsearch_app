package com.example.food_app.ui.result

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.food_app.data.model.*
import com.example.food_app.data.model.GourmetResponse
import com.example.food_app.data.repository.GourmetRepository
import com.example.food_app.interfaces.JsonPlaceHolder


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    lat: Double,
    lng: Double,
    range: Int,
    navController: NavController,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.searchShops(lat, lng, range)
    }

    // LazyListのスクロール状態を監視して、末尾に到達したら次のページを読み込む
    val isScrolledToEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                val viewportEndOffset = layoutInfo.viewportEndOffset
                lastVisibleItem.index + 1 == layoutInfo.totalItemsCount && lastVisibleItem.offset + lastVisibleItem.size <= viewportEndOffset
            }
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !uiState.isLoading && uiState.canLoadMore) {
            viewModel.loadNextPage()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("検索結果") },
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
                uiState.isLoading && uiState.shops.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "エラー: ${uiState.error}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.shops.isEmpty() -> {
                    Text(
                        text = "該当する店舗が見つかりませんでした。",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.shops) { shop ->
                            ShopItem(shop = shop) {
                                navController.navigate("detail/${shop.id}")
                            }
                        }
                        if (uiState.isLoading && uiState.shops.isNotEmpty()) {
                            item {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItem(shop: Shop, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = shop.photo.mobile.s,
                contentDescription = shop.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shop.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shop.access,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun ResultScreenPreview() {
    // プレビュー用にJsonPlaceHolderのフェイク実装を作成
    val fakeApi = object : JsonPlaceHolder {
        override suspend fun search(
            key: String, lat: Double, lng: Double, range: Int, start: Int, count: Int, format: String
        ): GourmetResponse {
            // ダミーのショップデータ
            val dummyShop = Shop(
                id = "1",
                name = "サンプル居酒屋",
                logoImage = "https://picsum.photos/",
                nameKana = "サンプルイザカヤ",
                address = "東京都千代田区",
                stationName = "東京",
                lat = 35.681236,
                lng = 139.767125,
                genre = Genre("居酒屋", "説明", "G001"),
                budget = Budget("B001", "3000円", "3000円"),
                catch = "キャッチコピー",
                capacity = "50",
                access = "東京駅徒歩5分",
                mobileAccess = "東京駅5分",
                urls = Urls("http://example.com"),
                photo = Photo(PcPhoto("", "", ""), MobilePhoto("", "")),
                open = "17:00-23:00",
                close = "年中無休",
                parking = "なし",
                other_memo = "特になし"
            )
            return GourmetResponse(
                Results(
                    apiVersion = "1.0",
                    results_available = 10,
                    results_returned = 10,
                    results_start = 1,
                    shop = listOf(dummyShop)
                )
            )
        }

        override suspend fun detail(key: String, id: String, format: String): GourmetResponse {
            return GourmetResponse(
                Results("1.0", 0, 0, 0, emptyList())
            )
        }
    }

    val fakeRepository = GourmetRepository(fakeApi)
    val fakeViewModel = ResultViewModel(fakeRepository)

    ResultScreen(
        lat = 35.681236,
        lng = 139.767125,
        range = 1,
        navController = rememberNavController(),
        viewModel = fakeViewModel
    )
}