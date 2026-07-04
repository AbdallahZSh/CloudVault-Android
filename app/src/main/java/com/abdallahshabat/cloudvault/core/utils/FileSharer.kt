package com.abdallahshabat.cloudvault.core.utils

import android.content.Context
import android.content.Intent
import com.abdallahshabat.cloudvault.data.model.CloudFile

/**
 * ------------------------------------------------------------
 * File Name : FileSharer.kt
 * Module    : Core
 * Project   : CloudVault
 *
 * English:
 * Shares uploaded files using Android Sharesheet.
 *
 * العربية:
 * مشاركة الملفات باستخدام Android Sharesheet.
 * ------------------------------------------------------------
 */
object FileSharer {

    /**
     * Shares a file.
     *
     * English:
     * Opens Android Sharesheet and shares
     * the Cloudinary URL.
     *
     * العربية:
     * فتح نافذة المشاركة الخاصة بأندرويد
     * وإرسال رابط الملف.
     */
    fun share(
        context: Context,
        file: CloudFile
    ) {

        val sendIntent = Intent(Intent.ACTION_SEND).apply {

            type = "text/plain"

            putExtra(
                Intent.EXTRA_SUBJECT,
                file.fileName
            )

            putExtra(
                Intent.EXTRA_TEXT,
                """
                 File: ${file.fileName} Download:${file.fileUrl} """.trimIndent()
            )

        }

        val chooser = Intent.createChooser(
            sendIntent,
            "Share File"
        )

        context.startActivity(chooser)

    }

}