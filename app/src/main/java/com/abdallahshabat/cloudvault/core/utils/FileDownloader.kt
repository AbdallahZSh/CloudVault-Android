package com.abdallahshabat.cloudvault.core.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.abdallahshabat.cloudvault.data.model.CloudFile
//سننفذ Download File بطريقة احترافية باستخدام Android DownloadManager. هذه هي الطريقة الرسمية التي تستخدمها أغلب التطبيقات.
/**
 * ------------------------------------------------------------
 * File Name : FileDownloader.kt
 * Module    : Core
 * Project   : CloudVault
 *
 * English:
 * Downloads files using Android DownloadManager.
 *
 * العربية:
 * تحميل الملفات باستخدام DownloadManager الرسمي.
 * ------------------------------------------------------------
 */
object FileDownloader {

    /**
     * Downloads a file.
     *
     * English:
     * Starts downloading the selected file.
     *
     * العربية:
     * بدء تحميل الملف المحدد.
     */
    fun download(
        context: Context,
        file: CloudFile
    ) {

        try {

            val request = DownloadManager.Request(
                Uri.parse(file.fileUrl)
            )

            request.setTitle(file.fileName)

            request.setDescription("Downloading file...")

            request.setNotificationVisibility(
                DownloadManager.Request
                    .VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )

            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                file.fileName
            )

            val manager =
                context.getSystemService(
                    Context.DOWNLOAD_SERVICE
                ) as DownloadManager

            manager.enqueue(request)

            Toast.makeText(
                context,
                "Download started.",
                Toast.LENGTH_SHORT
            ).show()
            NotificationHelper.addNotification(

                file.ownerId,

                "Download Started",

                "${file.fileName} download has started.",

                "download"

            )
        } catch (e: Exception) {

            Toast.makeText(
                context,
                "Failed to start download.",
                Toast.LENGTH_LONG
            ).show()

        }

    }

}