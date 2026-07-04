package com.abdallahshabat.cloudvault.data.model

/**
 * ------------------------------------------------------------
 * File Name : UploadResult.kt
 * Module    : Data
 * Project   : CloudVault
 *
 * English:
 * Contains the information returned after uploading
 * a file to Cloudinary.
 *
 * العربية:
 * يحتوي على البيانات الناتجة بعد رفع الملف
 * إلى Cloudinary.
 * ------------------------------------------------------------
 */
data class UploadResult(

    /**
     * Cloudinary file URL.
     */
    val fileUrl: String,

    /**
     * Cloudinary public identifier.
     */
    val publicId: String

)