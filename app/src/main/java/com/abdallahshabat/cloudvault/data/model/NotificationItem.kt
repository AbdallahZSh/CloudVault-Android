package com.abdallahshabat.cloudvault.data.model

data class NotificationItem(

    val id: String = "",

    val title: String = "",

    val message: String = "",

    val type: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val isRead: Boolean = false
)