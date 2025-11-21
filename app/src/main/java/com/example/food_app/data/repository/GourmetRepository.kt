package com.example.food_app.data.repository

import com.example.food_app.data.model.GourmetResponse
import com.example.food_app.interfaces.JsonPlaceHolder
import javax.inject.Inject

class GourmetRepository @Inject constructor(
    private val apiService: JsonPlaceHolder
) {
    suspend fun searchGourmet(
        lat: Double, 
        lng: Double, 
        range: Int, 
        code: String? = null, // 引数名をcodeに変更
        start: Int, 
        count: Int
    ): GourmetResponse {
        return apiService.search(
            lat = lat, 
            lng = lng, 
            range = range, 
            code = code, // API呼び出しもcode引数を使用
            start = start, 
            count = count
        )
    }

    suspend fun fetchGourmetDetail(id: String): GourmetResponse {
        return apiService.detail(id = id)
    }
}
