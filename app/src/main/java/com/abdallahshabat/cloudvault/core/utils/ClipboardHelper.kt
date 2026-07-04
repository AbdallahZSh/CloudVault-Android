package com.abdallahshabat.cloudvault.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.abdallahshabat.cloudvault.data.model.CloudFile

/**
 * ------------------------------------------------------------
 * File Name : ClipboardHelper.kt
 * Module    : Core
 * Project   : CloudVault
 *
 * English:
 * Helper class responsible for copying file links
 * to the system clipboard.
 *
 * العربية:
 * كلاس مساعد مسؤول عن نسخ رابط الملف
 * إلى الحافظة.
 * ------------------------------------------------------------
 */
object ClipboardHelper {

    /**
     * Copies the Cloudinary URL to clipboard.
     *
     * English:
     * Copies the selected file URL.
     *
     * العربية:
     * نسخ رابط الملف إلى الحافظة.
     */
    fun copyLink(
        context: Context,
        file: CloudFile
    ) {

        val clipboard =
            context.getSystemService(
                Context.CLIPBOARD_SERVICE
            ) as ClipboardManager

        val clip = ClipData.newPlainText(
            file.fileName,
            file.fileUrl
        )

        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            context,
            "Link copied to clipboard.",
            Toast.LENGTH_SHORT
        ).show()

    }

}