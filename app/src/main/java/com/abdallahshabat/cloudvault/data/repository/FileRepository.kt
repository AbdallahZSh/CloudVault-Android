package com.abdallahshabat.cloudvault.data.repository

import android.content.Context
import android.net.Uri
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener

interface FileRepository {

    suspend fun uploadFile(
        context: Context,
        userId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long,
        listener: UploadProgressListener
    ): Result<Unit>

    suspend fun deleteFile(
        cloudFile: CloudFile
    ): Result<Unit>

    suspend fun getFiles(
        userId: String
    ): Result<List<CloudFile>>
    /**
     * ------------------------------------------------------------
     * Updates file name.
     *
     * English:
     * Renames an existing file.
     *
     * العربية:
     * تحديث اسم الملف.
     * ------------------------------------------------------------
     */
    suspend fun renameFile(
        cloudFile: CloudFile,
        newName: String
    ): Result<Unit>

}