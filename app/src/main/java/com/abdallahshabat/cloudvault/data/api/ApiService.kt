package com.abdallahshabat.cloudvault.data.api

import com.abdallahshabat.cloudvault.data.model.AuthResponse
import com.abdallahshabat.cloudvault.data.model.LoginRequest
import com.abdallahshabat.cloudvault.data.model.RegisterRequest
import com.abdallahshabat.cloudvault.data.model.UserModel
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserModel>
}