package com.example.food_app.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // 位置情報パーミッションの状態
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

    // 現在地を取得する関数
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (locationPermissionState.status.isGranted) {
            isLoadingLocation = true
            locationError = null
            scope.launch {
                try {
                    // 優先度高精度で現在地を取得
                    val priority = Priority.PRIORITY_HIGH_ACCURACY
                    val result = fusedLocationClient.getCurrentLocation(
                        priority,
                        CancellationTokenSource().token
                    ).await()

                    if (result != null) {
                        currentLocation = Pair(result.latitude, result.longitude)
                    } else {
                        locationError = "現在地を取得できませんでした"
                    }
                } catch (e: Exception) {
                    locationError = "エラーが発生しました: ${e.localizedMessage}"
                } finally {
                    isLoadingLocation = false
                }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
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

            // 現在地情報の表示
            if (isLoadingLocation) {
                CircularProgressIndicator()
                Text("現在地を取得中...")
            } else if (currentLocation != null) {
                Text("取得済み: 緯度=${String.format("%.4f", currentLocation!!.first)}, 経度=${String.format("%.4f", currentLocation!!.second)}")
            } else {
                Text("現在地は未取得です")
            }

            if (locationError != null) {
                Text(
                    text = locationError!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 検索半径の選択
            RangeSelector(
                selectedRange = viewModel.selectedRange,
                onRangeSelected = { viewModel.selectedRange = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 現在地取得ボタン
            if (currentLocation == null) {
                Button(
                    onClick = { getCurrentLocation() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("現在地を取得する")
                }
            } else {
                // 検索ボタン
                Button(
                    onClick = {
                        currentLocation?.let { loc ->
                            navController.navigate("result/${loc.first}/${loc.second}/${viewModel.selectedRange.value}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("この場所周辺を検索する")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { getCurrentLocation() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("現在地を更新する")
                }
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
                .menuAnchor()
                .fillMaxWidth()
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