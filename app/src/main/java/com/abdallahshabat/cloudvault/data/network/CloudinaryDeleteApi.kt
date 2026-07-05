package com.abdallahshabat.cloudvault.data.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//لخطوة الأولى: إنشاء API للحذف
//كما أنشأنا uploadImage()، سننشئ API للحذف.

interface CloudinaryDeleteApi {

    @FormUrlEncoded
    @POST("destroy")
    suspend fun deleteFile(

        @Field("public_id")
        publicId: String,

        @Field("api_key")
        apiKey: String,

        @Field("timestamp")
        timestamp: Long,

        @Field("signature")
        signature: String

    ): Response<Unit>

}