package com.abdallahshabat.cloudvault.core.validation

import android.util.Patterns

/**
 * ------------------------------------------------------------
 * File Name : AuthValidator.kt
 * Module    : Validation
 * Project   : CloudVault
 *
 * English:
 * Provides reusable validation methods for authentication
 * screens such as Login, Register and Reset Password.
 *
 * العربية:
 * يوفر دوال للتحقق من صحة البيانات الخاصة بالمصادقة
 * مثل تسجيل الدخول وإنشاء الحساب واستعادة كلمة المرور.
 * ------------------------------------------------------------
 */
object AuthValidator {

    /**
     * English:
     * Validates the user's full name.
     *
     * العربية:
     * التحقق من صحة الاسم الكامل.
     */
    fun validateFullName(fullName: String): ValidationResult {

        return when {
            fullName.isBlank() ->
                ValidationResult.Invalid("Full name is required.")

            fullName.length < 3 ->
                ValidationResult.Invalid("Full name is too short.")

            else ->
                ValidationResult.Valid
        }

    }

    /**
     * English:
     * Validates email format.
     *
     * العربية:
     * التحقق من صحة البريد الإلكتروني.
     */
    fun validateEmail(email: String): ValidationResult {

        return when {
            email.isBlank() ->
                ValidationResult.Invalid("Email is required.")

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Invalid("Invalid email address.")

            else ->
                ValidationResult.Valid
        }

    }

    /**
     * English:
     * Validates password strength.
     *
     * العربية:
     * التحقق من قوة كلمة المرور.
     */
    fun validatePassword(password: String): ValidationResult {

        return when {

            password.isBlank() ->
                ValidationResult.Invalid("Password is required.")

            password.length < 8 ->
                ValidationResult.Invalid("Password must contain at least 8 characters.")

            else ->
                ValidationResult.Valid

        }

    }

    /**
     * English:
     * Checks whether both passwords match.
     *
     * العربية:
     * التحقق من تطابق كلمتي المرور.
     */
    fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): ValidationResult {

        return when {

            confirmPassword.isBlank() ->
                ValidationResult.Invalid("Please confirm your password.")

            password != confirmPassword ->
                ValidationResult.Invalid("Passwords do not match.")

            else ->
                ValidationResult.Valid

        }

    }

}