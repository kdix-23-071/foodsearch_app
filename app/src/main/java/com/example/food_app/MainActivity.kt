package com.example.food_app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.food_app.BuildConfig.HOT_PEPPER_API_KEY
import com.example.food_app.interfaces.JsonPlaceHolder
import com.example.food_app.ui.theme.Food_appTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.lang.Exception

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Food_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://webservice.recruit.co.jp/hotpepper/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val hotPepperAPI = retrofit.create<JsonPlaceHolder>()

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            try {
                // APIにリクエストを送信
                val response = hotPepperAPI.search(
                    key = HOT_PEPPER_API_KEY,
                    lat = 34.6476217, // 東京駅の緯度
                    lng = 135.5909042, // 東京駅の経度
                    range = 3,
                    start = 1,
                    count = 10
                )
                // 成功ログ
                Log.d(TAG, "API Response: $response")
            } catch (e: Exception) {
                // エラーログ
                Log.e(TAG, "API call failed", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Food_appTheme {
        Greeting("Android")
    }
}