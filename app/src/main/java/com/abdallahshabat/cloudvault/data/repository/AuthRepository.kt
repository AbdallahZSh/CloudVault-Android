package com.abdallahshabat.cloudvault.data.repository

import com.abdallahshabat.cloudvault.core.network.RetrofitClient
import com.abdallahshabat.cloudvault.data.model.LoginRequest
import com.abdallahshabat.cloudvault.data.model.RegisterRequest

class AuthRepository {

    private val api = RetrofitClient.instance

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))

    suspend fun register(name: String, email: String, password: String) =
        api.register(RegisterRequest(name, email, password))
}