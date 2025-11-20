package com.example.food_app.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    // 固定の現在地（東京駅）
    val currentLocation = remember {
        object {
            val latitude = 35.681236
            val longitude = 139.767125
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("店舗検索") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "現在地からお店を探す",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 現在地情報表示
            //TODO 試験的に表示しているので実装終了時に削除
            Text("現在地: 緯度=${String.format("%.4f", currentLocation.latitude)}, 経度=${String.format("%.4f", currentLocation.longitude)}")

            Spacer(modifier = Modifier.height(16.dp))

            // 検索半径の選択
            RangeSelector(
                selectedRange = viewModel.selectedRange,
                onRangeSelected = { viewModel.selectedRange = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate("result/${currentLocation.latitude}/${currentLocation.longitude}/${viewModel.selectedRange.value}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("この条件で検索する")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSelector(
    selectedRange: SearchViewModel.Range,
    onRangeSelected: (SearchViewModel.Range) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val items = SearchViewModel.Range.entries
    //検索範囲選択のドロップメニューボックス
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = "検索半径: ${selectedRange.label}",
            onValueChange = {},
            readOnly = true,
            label = { Text("検索半径") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.label) },
                    onClick = {
                        onRangeSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController(), viewModel = SearchViewModel())
}