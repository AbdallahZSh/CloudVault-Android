package com.abdallahshabat.cloudvault.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeleteRetrofit {

    private const val BASE_URL = "http://10.0.2.2:5199/"

    val api: DeleteApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeleteApi::class.java)

    }

}