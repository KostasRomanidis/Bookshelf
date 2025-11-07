package com.kroman.bookshelf.di

import android.util.Log
import com.kroman.bookshelf.BuildConfig
import com.kroman.bookshelf.data.remote.api.BooksApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    singleOf(::provideHttpLoggingInterceptor)
    singleOf(::provideOkHttpClient)
    singleOf(::provideRetrofit)
    singleOf(::getBooksApi)
}

val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor { Log.d("OkHttp", it) }.apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

fun getBooksApi(retrofit: Retrofit): BooksApi = retrofit.create(BooksApi::class.java)

