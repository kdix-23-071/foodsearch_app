package com.example.food_app.interfaces

import com.example.food_app.BuildConfig
import com.example.food_app.data.GourmetResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonPlaceHolder {
    companion object {
        private const val HOT_PEPPER_API_KEY = BuildConfig.HOT_PEPPER_API_KEY
    }

    @GET("gourmet/v1/")
    suspend fun search(
        @Query("key") key: String = HOT_PEPPER_API_KEY,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("range") range: Int,
        @Query("start") start: Int,
        @Query("count") count: Int,
        @Query("format") format: String = "json"
    ): GourmetResponse

    @GET("gourmet/v1/")
    suspend fun detail(
        @Query("key") key: String = HOT_PEPPER_API_KEY,
        @Query("id") id: String,
        @Query("format") format: String = "json"
    ): GourmetResponse
}