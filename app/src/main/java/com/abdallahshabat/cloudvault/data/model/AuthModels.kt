package com.abdallahshabat.cloudvault.data.model

import com.google.firebase.firestore.PropertyName

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

data class RegisterForm(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

/**
 * ------------------------------------------------------------
 * CloudVault
 * CloudFile Model
 * ------------------------------------------------------------
 *
 * Represents a file uploaded by the user.
 * Stored in Firestore and displayed across the app.
 */
data class CloudFile(

    /** Firestore document id */
    val id: String = "",

    /** Owner uid */
    val ownerId: String = "",

    /** File name */
    val fileName: String = "",

    /** Cloudinary URL */
    val fileUrl: String = "",

    /** Cloudinary public id */
    val publicId: String = "",

    /** MIME type */
    val fileType: String = "",

    /** Size in bytes */
    val fileSize: Long = 0L,

    /** Upload timestamp */
    val uploadedAt: Long = System.currentTimeMillis(),

    /** Favorite status */
    @get:PropertyName("isFavorite")
    @set:PropertyName("isFavorite")
    var isFavorite: Boolean = false
) {

    /**
     * Returns true if the file is an image.
     */
    fun isImage(): Boolean =
        fileType.startsWith("image")

    /**
     * Returns true if the file is a PDF.
     */
    fun isPdf(): Boolean =
        fileType.contains("pdf", ignoreCase = true)

    /**
     * Returns readable file size.
     */
    fun formattedSize(): String =
        when {
            fileSize >= 1024L * 1024L * 1024L ->
                String.format("%.2f GB", fileSize / 1024f / 1024f / 1024f)

            fileSize >= 1024L * 1024L ->
                String.format("%.2f MB", fileSize / 1024f / 1024f)

            fileSize >= 1024L ->
                String.format("%.2f KB", fileSize / 1024f)

            else ->
                "$fileSize Bytes"
        }
}