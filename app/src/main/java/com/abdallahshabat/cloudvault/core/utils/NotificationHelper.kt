package com.abdallahshabat.cloudvault.core.utils

import android.util.Log
import com.abdallahshabat.cloudvault.data.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object NotificationHelper {

    private val firestore = FirebaseFirestore.getInstance()

    fun addNotification(
        userId: String,
        title: String,
        message: String,
        type: String
    ) {
        Log.d("NotificationHelper", "Saving notification...")

        val notification = NotificationItem(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            type = type,
            createdAt = System.currentTimeMillis(),
            isRead = false
        )

        firestore
            .collection("users")
            .document(userId)
            .collection("notifications")
            .document(notification.id)
            .set(notification)
            .addOnSuccessListener {

                Log.d(
                    "NotificationHelper",
                    "Notification saved successfully."
                )

            }
            .addOnFailureListener { e ->

                Log.e(
                    "NotificationHelper",
                    "Failed: ${e.message}"
                )

            }

    }

}