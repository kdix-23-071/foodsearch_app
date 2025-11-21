package com.example.food_app.di

import com.example.food_app.interfaces.JsonPlaceHolder
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // アプリ全体で使えるようにする
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // Gsonの設定
        val gson = GsonBuilder()
            .setLenient() // HTMLなどが返ってきた時のパースエラーを少し緩和する設定
            .create()

        return Retrofit.Builder()
            .baseUrl("https://webservice.recruit.co.jp/hotpepper/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideJsonPlaceHolder(retrofit: Retrofit): JsonPlaceHolder {
        return retrofit.create(JsonPlaceHolder::class.java)
    }
}
