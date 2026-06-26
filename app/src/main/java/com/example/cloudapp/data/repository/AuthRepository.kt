package com.example.cloudapp.data.repository

import com.example.cloudapp.data.api.RetrofitClient
import com.example.cloudapp.data.model.LoginRequest
import com.example.cloudapp.data.model.RegisterRequest

class AuthRepository {

    private val api = RetrofitClient.instance

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))

    suspend fun register(name: String, email: String, password: String) =
        api.register(RegisterRequest(name, email, password))
}