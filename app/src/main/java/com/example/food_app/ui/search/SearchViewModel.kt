package com.example.food_app.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    // 検索範囲の選択肢
    enum class Range(val value: Int, val label: String) {
        RANGE_300(1, "300m"),
        RANGE_500(2, "500m"),
        RANGE_1000(3, "1000m"), // Default
        RANGE_2000(4, "2000m"),
        RANGE_3000(5, "3000m");

        companion object {
            fun fromValue(value: Int): Range? {
                return entries.find { it.value == value }
            }
        }
    }

    // 選択されている検索範囲（初期値: 1000m）
    var selectedRange by mutableStateOf(Range.RANGE_1000)
}