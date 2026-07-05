package com.abdallahshabat.cloudvault.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryDeleteRetrofit {

    private const val BASE_URL =
        "https://api.cloudinary.com/v1_1/muydjqhs/image/"

    val api: CloudinaryDeleteApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(CloudinaryDeleteApi::class.java)

    }

}