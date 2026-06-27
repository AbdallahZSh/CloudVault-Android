package com.abdallahshabat.cloudvault.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserModel
)

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val storageUsed: Long,
    val storageTotal: Long
)