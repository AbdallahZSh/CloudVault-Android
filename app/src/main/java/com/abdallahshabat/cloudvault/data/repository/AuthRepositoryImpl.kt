package com.abdallahshabat.cloudvault.data.repository

import com.abdallahshabat.cloudvault.data.model.User
import com.abdallahshabat.cloudvault.data.remote.FirebaseAuthDataSource
import com.google.firebase.auth.FirebaseAuth

/**
 * File Name : AuthRepositoryImpl.kt
 * Module    : Authentication
 * Project   : CloudVault

 * Concrete implementation of AuthRepository.
 * This class communicates with Firebase Authentication
 * through FirebaseAuthDataSource.

 * التطبيق الفعلي لـ AuthRepository.
 * هذا الملف مسؤول عن التواصل مع Firebase Authentication
 * من خلال FirebaseAuthDataSource.
 */
class AuthRepositoryImpl : AuthRepository {

    // Firebase data source.
    // مصدر البيانات الخاص بـ Firebase.
    private val firebaseDataSource = FirebaseAuthDataSource()


    //Registers a new user.
    //إنشاء حساب مستخدم جديد.
    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<Unit> {
        val result = firebaseDataSource.register(
            fullName,
            email,
            password
        )
        return result.fold(
            onSuccess = {
                // لاحقاً سنحفظ fullName في Firestore
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    // Logs in an existing user.
    // تسجيل دخول مستخدم موجود
    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return firebaseDataSource.login(
            email,
            password
        )
    }

    override suspend fun getCurrentUser(): Result<User> {
        return firebaseDataSource.getCurrentUser()
    }

    override fun logout() {
        FirebaseAuth.getInstance().signOut()

    }

}