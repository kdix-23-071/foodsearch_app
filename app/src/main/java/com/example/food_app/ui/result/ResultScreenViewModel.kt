package com.example.food_app.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_app.data.model.GourmetResponse
import com.example.food_app.data.model.Shop
import com.example.food_app.data.repository.GourmetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val shops: List<Shop> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canLoadMore: Boolean = true
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: GourmetRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    private var currentRange: Int = 3

    fun searchShops(lat: Double, lng: Double, range: Int) {
        // 新しい検索条件の場合、リセット
        if (lat != currentLat || lng != currentLng || range != currentRange) {
            resetAndSearch(lat, lng, range)
        } else if (_uiState.value.shops.isEmpty()) {
            // 初回ロード
            loadShops()
        }
    }

    private fun resetAndSearch(lat: Double, lng: Double, range: Int) {
        currentLat = lat
        currentLng = lng
        currentRange = range
        currentPage = 1
        _uiState.update { ResultUiState() } // 状態をリセット
        loadShops()
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading || !_uiState.value.canLoadMore) return
        currentPage++
        loadShops()
    }


    private fun loadShops() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = repository.searchGourmet(
                    lat = currentLat,
                    lng = currentLng,
                    range = currentRange,
                    start = (_uiState.value.shops.size) + 1,
                    count = 10
                )
                val newShops = response.results.shop
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        shops = (currentState.shops + newShops) as List<Shop>,
                        canLoadMore = (currentState.shops.size + newShops.size) < response.results.results_available
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "不明なエラー") }
            }
        }
    }
}
