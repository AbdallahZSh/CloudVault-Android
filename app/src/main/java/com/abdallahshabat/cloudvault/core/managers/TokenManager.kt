package com.abdallahshabat.cloudvault.core.managers

import android.content.Context
import com.abdallahshabat.cloudvault.data.model.User
import com.google.gson.Gson

object TokenManager {

    private const val PREF_NAME = "cloudvault_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER = "auth_user"

    // ───────────────────────── Token ─────────────────────────

    fun saveToken(
        context: Context,
        token: String
    ) {
        prefs(context)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(context: Context): String? {
        return prefs(context).getString(KEY_TOKEN, null)
    }

    fun getBearerToken(context: Context): String {
        return "Bearer ${getToken(context)}"
    }

    fun hasToken(context: Context): Boolean {
        return getToken(context) != null
    }

    // ───────────────────────── User ─────────────────────────

    fun saveUser(
        context: Context,
        user: User
    ) {

        val json = Gson().toJson(user)

        prefs(context)
            .edit()
            .putString(KEY_USER, json)
            .apply()

    }

    fun getUser(context: Context): User? {

        val json = prefs(context)
            .getString(KEY_USER, null)
            ?: return null

        return try {

            Gson().fromJson(
                json,
                User::class.java
            )

        } catch (e: Exception) {

            null

        }

    }

    // ───────────────────────── Logout ─────────────────────────

    fun clear(context: Context) {

        prefs(context)
            .edit()
            .clear()
            .apply()

    }

    // ───────────────────────── Helper ─────────────────────────

    private fun prefs(context: Context) =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

}