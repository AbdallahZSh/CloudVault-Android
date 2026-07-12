package com.abdallahshabat.cloudvault.data.repository

import android.content.Context
import android.net.Uri
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener

/**
 * ------------------------------------------------------------
 * FileRepository
 * ------------------------------------------------------------
 *
 * The Repository contract responsible for all file operations.
 *
 * It hides Firestore and Cloudinary implementation details
 * from the ViewModels.
 *
 * جميع العمليات الخاصة بالملفات تمر من خلال هذه الواجهة.
 * ------------------------------------------------------------
 */
interface FileRepository {

    /**
     * Upload a new file.
     */
    suspend fun uploadFile(
        context: Context,
        userId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long,
        listener: UploadProgressListener
    ): Result<CloudFile>

    /**
     * Delete file from Cloudinary and Firestore.
     */
    suspend fun deleteFile(
        cloudFile: CloudFile
    ): Result<Unit>

    /**
     * Get all files of the current user.
     */
    suspend fun getFiles(
        userId: String
    ): Result<List<CloudFile>>

    /**
     * Rename an existing file.
     */
    suspend fun renameFile(
        cloudFile: CloudFile,
        newName: String
    ): Result<Unit>

    /**
     * Set favorite state.
     */
    suspend fun setFavorite(
        cloudFile: CloudFile,
        isFavorite: Boolean
    ): Result<Unit>

    /**
     * Toggle favorite state.
     */
    suspend fun toggleFavorite(
        cloudFile: CloudFile
    ): Result<Unit>

    /**
     * Get only favorite files.
     */
    suspend fun getFavoriteFiles(
        userId: String
    ): Result<List<CloudFile>>

    suspend fun markAllNotificationsAsRead(
        userId: String
    ): Result<Unit>

    suspend fun searchFiles(
        userId: String,
        query: String
    ): Result<List<CloudFile>>
}