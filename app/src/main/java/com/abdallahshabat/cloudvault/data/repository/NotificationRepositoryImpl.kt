package com.abdallahshabat.cloudvault.data.repository

import com.abdallahshabat.cloudvault.data.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImpl : NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getNotifications(
        userId: String
    ): Result<List<NotificationItem>> {

        return try {

            val result = firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            Result.success(
                result.toObjects(NotificationItem::class.java)
            )

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    override suspend fun getUnreadCount(
        userId: String
    ): Result<Int> {

        return try {

            val result = firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .whereEqualTo("isRead", false)
                .get()
                .await()

            Result.success(result.size())

        } catch (e: Exception) {

            Result.failure(e)

        }

    }
    override suspend fun markAllAsRead(
        userId: String
    ): Result<Unit> {

        return try {

            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()

            snapshot.documents.forEach {

                batch.update(
                    it.reference,
                    "isRead",
                    true
                )

            }

            batch.commit().await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }
    override suspend fun addNotification(
        userId: String,
        notification: NotificationItem
    ): Result<Unit> {

        return try {

            firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .document(notification.id)
                .set(notification)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    override suspend fun markAsRead(
        userId: String,
        notificationId: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .document(notificationId)
                .update("isRead", true)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    override suspend fun deleteNotification(
        userId: String,
        notificationId: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .document(notificationId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    override suspend fun clearAll(
        userId: String
    ): Result<Unit> {

        return try {

            val notifications = firestore
                .collection("users")
                .document(userId)
                .collection("notifications")
                .get()
                .await()

            notifications.documents.forEach {

                it.reference.delete()

            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

}