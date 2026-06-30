package com.abdallahshabat.cloudvault.data.repository

import com.abdallahshabat.cloudvault.data.model.User

/**
 * ------------------------------------------------------------
 * File Name : AuthRepository.kt
 * Module    : Authentication
 * Project   : CloudVault
 *
 * English:
 * Defines authentication operations used by the ViewModel.
 * The implementation can use Firebase or any other backend.
 *
 * العربية:
 * يعرّف عمليات المصادقة التي يستخدمها الـ ViewModel.
 * ويمكن تنفيذها بواسطة Firebase أو أي خادم آخر.
 * ------------------------------------------------------------
 */
interface AuthRepository {

    /**
     * ---------------------------------------------------------
     * English:
     * Registers a new user.
     *
     * العربية:
     * إنشاء حساب مستخدم جديد.
     * ---------------------------------------------------------
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<Unit>

    /**
     * ---------------------------------------------------------
     * English:
     * Logs in an existing user.
     *
     * العربية:
     * تسجيل دخول مستخدم موجود.
     * ---------------------------------------------------------
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    fun logout()
}