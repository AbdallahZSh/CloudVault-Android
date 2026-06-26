package com.example.cloudapp.data.model

data class FileItem(
    val id: String,
    val name: String,
    val size: Long,
    val type: String,
    val url: String,
    val createdAt: String
)