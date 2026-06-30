package com.abdallahshabat.cloudvault.core.validation

/**
 * ------------------------------------------------------------
 * File Name : ValidationResult.kt
 * Module    : Validation
 * Project   : CloudVault
 *
 * English:
 * Represents the result of a validation operation.
 *
 * العربية:
 * يمثل نتيجة عملية التحقق من صحة البيانات.
 * ------------------------------------------------------------
 */
sealed class ValidationResult {

    /**
     * English:
     * Validation passed successfully.
     *
     * العربية:
     * التحقق ناجح.
     */
    data object Valid : ValidationResult()

    /**
     * English:
     * Validation failed with an error message.
     *
     * العربية:
     * فشل التحقق مع رسالة خطأ.
     */
    data class Invalid(
        val message: String
    ) : ValidationResult()

}