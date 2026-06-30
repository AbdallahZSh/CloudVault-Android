package com.abdallahshabat.cloudvault.data.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val storageUsed: Long = 0L,
    val storageTotal: Long = 10L * 1024 * 1024 * 1024L // 10 GB
)