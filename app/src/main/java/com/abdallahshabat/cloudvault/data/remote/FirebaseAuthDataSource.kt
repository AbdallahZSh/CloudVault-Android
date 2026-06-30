package com.abdallahshabat.cloudvault.data.remote


import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.abdallahshabat.cloudvault.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ------------------------------------------------------------
 * File Name : FirebaseAuthDataSource.kt
 * Module    : Authentication
 * Project   : CloudVault
 *
 * English:
 * Handles direct communication with Firebase Authentication.
 * This class is the only layer that knows Firebase APIs.
 *
 * العربية:
 * مسؤول عن التواصل المباشر مع Firebase Authentication.
 * هذا الملف هو الوحيد الذي يتعامل مع Firebase مباشرة.
 * ------------------------------------------------------------
 */
class FirebaseAuthDataSource {


    //Firebase Authentication instance.
    // نسخة Firebase Authentication.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    //Creates a new user using email and password.
    //إنشاء مستخدم جديد باستخدام البريد الإلكتروني وكلمة المرور.
    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            val authResult = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("User ID not found"))

            val user = User(
                uid = uid,
                fullName = fullName,
                email = email
            )

            firestore
                .collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

   //Signs in an existing user.
    //تسجيل دخول مستخدم موجود.
   suspend fun login(
       email: String,
       password: String
   ): Result<Unit> {

       return try {

           auth.signInWithEmailAndPassword(
               email,
               password
           ).await()

           Result.success(Unit)

       } catch (e: Exception) {

           Result.failure(e)

       }
   }
    suspend fun getCurrentUser(): Result<User> {

        return try {

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("User data not found"))

            Result.success(user)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }
}