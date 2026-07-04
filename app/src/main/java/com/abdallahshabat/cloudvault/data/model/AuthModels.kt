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

data class RegisterForm(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
/**
 * ------------------------------------------------------------
 * File Name : CloudFile.kt
 * Module    : File Management
 * Project   : CloudVault
 *
 * English:
 * Represents a file uploaded by the user.
 * This model is stored inside Firestore and displayed
 * throughout the application.
 *
 * العربية:
 * يمثل ملفاً قام المستخدم برفعه.
 * يتم حفظه داخل Firestore واستخدامه في جميع أجزاء التطبيق.
 * ------------------------------------------------------------
 */

data class CloudFile(

    /**
     * Firestore document id.
     * معرف المستند داخل Firestore.
     */
    val id: String = "",

    /**
     * Owner user id.
     * معرف صاحب الملف.
     */
    val ownerId: String = "",

    /**
     * Original file name.
     * الاسم الأصلي للملف.
     */
    val fileName: String = "",

    /**
     * Cloudinary download URL.
     * رابط الملف على Cloudinary.
     */
    val fileUrl: String = "",
    /**
     * Cloudinary public identifier.
     *
     * English:
     * Used to delete or manage the uploaded file.
     *
     * العربية:
     * المعرف الخاص بالملف داخل Cloudinary.
     * يستخدم للحذف وإدارة الملف.
     */
    val publicId: String = "",

    /**
     * MIME type.
     * نوع الملف.
     */
    val fileType: String = "",

    /**
     * File size in bytes.
     * حجم الملف بالبايت.
     */
    val fileSize: Long = 0L,

    /**
     * Upload date.
     * تاريخ الرفع.
     */
    val uploadedAt: Long = System.currentTimeMillis()

)