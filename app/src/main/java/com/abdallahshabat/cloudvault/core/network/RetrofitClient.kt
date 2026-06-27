package com.abdallahshabat.cloudvault.core.network

import com.abdallahshabat.cloudvault.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
/*لأن RetrofitClient ليس جزءًا من البيانات.
هو مسؤول عن إعداد الشبكة بالكامل.
حتى لو حذفنا Authentication غدًا، سيبقى RetrofitClient كما هو.*/
object RetrofitClient {

    private const val BASE_URL = "https://yourserver.com/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}