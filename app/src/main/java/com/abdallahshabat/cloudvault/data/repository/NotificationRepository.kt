package com.abdallahshabat.cloudvault.data.repository

import com.abdallahshabat.cloudvault.data.model.NotificationItem

interface NotificationRepository {

    suspend fun getNotifications(
        userId: String
    ): Result<List<NotificationItem>>

    suspend fun getUnreadCount(
        userId: String
    ): Result<Int>

    suspend fun markAllAsRead(
        userId: String
    ): Result<Unit>

    suspend fun addNotification(
        userId: String,
        notification: NotificationItem
    ): Result<Unit>

    suspend fun markAsRead(
        userId: String,
        notificationId: String
    ): Result<Unit>

    suspend fun deleteNotification(
        userId: String,
        notificationId: String
    ): Result<Unit>

    suspend fun clearAll(
        userId: String
    ): Result<Unit>

}