package com.example.food_app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GourmetResponse(
    val results: Results
)

@Serializable
data class Results(
    @SerialName("api_version") val apiVersion: String,
    @SerialName("results_available") val results_available: Int,
    @SerialName("results_returned") val results_returned: Int,
    @SerialName("results_start") val results_start: Int,
    val shop: List<Shop> = emptyList()
)

//お店の詳細情報リスト
@Serializable
data class Shop(
    val id: String, //店舗ID
    val name: String, //店舗名
    @SerialName("logo_image") val logoImage: String,
    @SerialName("name_kana") val nameKana: String,
    val address: String,
    @SerialName("station_name") val stationName: String,
    val lat: Double,
    val lng: Double,
    val genre: Genre,
    val budget: Budget,
    val `catch`: String,
    val capacity: String = "", // IntからStringに変更し、空文字対策
    val access: String,
    val other_memo: String = "",
    val parking: String = "",
    @SerialName("mobile_access") val mobileAccess: String,
    val urls: Urls,
    val photo: Photo,
    val open: String,
    val close: String
)

@Serializable
data class Genre(
    val name: String,
    val `catch`: String,
    val code: String
)

@Serializable
data class Budget(
    val code: String,
    val name: String,
    val average: String
)

@Serializable
data class Urls(
    val pc: String
)

@Serializable
data class Photo(
    val pc: PcPhoto,
    val mobile: MobilePhoto
)

@Serializable
data class PcPhoto(
    val l: String,
    val m: String,
    val s: String
)

@Serializable
data class MobilePhoto(
    val l: String,
    val s: String
)
