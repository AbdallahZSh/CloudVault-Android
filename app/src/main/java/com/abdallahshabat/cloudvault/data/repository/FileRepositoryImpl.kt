package com.abdallahshabat.cloudvault.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.network.DeleteFileRequest
import com.abdallahshabat.cloudvault.data.network.DeleteRetrofit
import com.abdallahshabat.cloudvault.data.remote.CloudinaryDataSource
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FileRepositoryImpl : FileRepository {

    companion object {
        private const val TAG = "FileRepository"
    }

    private val firestore = FirebaseFirestore.getInstance()

    private val cloudinary = CloudinaryDataSource()

    /**
     * Returns user files collection.
     */
    private fun userFiles(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("files")

    override suspend fun uploadFile(
        context: Context,
        userId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long,
        listener: UploadProgressListener
    ): Result<Unit> {

        return try {

            cloudinary.uploadImage(
                context,
                fileUri,
                listener
            ).fold(

                onSuccess = { result ->

                    val document = userFiles(userId).document()

                    val cloudFile = CloudFile(
                        id = document.id,
                        ownerId = userId,
                        fileName = fileName,
                        fileUrl = result.fileUrl,
                        publicId = result.publicId,
                        fileType = fileType,
                        fileSize = fileSize
                    )

                    document.set(cloudFile).await()

                    Result.success(Unit)

                },

                onFailure = {

                    Result.failure(it)

                }

            )

        } catch (e: Exception) {

            Log.e(TAG, "Upload failed", e)

            Result.failure(e)

        }

    }

    override suspend fun getFiles(
        userId: String
    ): Result<List<CloudFile>> {

        return try {

            val snapshot = userFiles(userId)
                .orderBy(
                    "uploadedAt",
                    Query.Direction.DESCENDING
                )
                .get()
                .await()

            Result.success(
                snapshot.toObjects(CloudFile::class.java)
            )

        } catch (e: Exception) {

            Log.e(TAG, "Load files failed", e)

            Result.failure(e)

        }

    }

    override suspend fun deleteFile(
        cloudFile: CloudFile
    ): Result<Unit> {

        return try {

            val response = DeleteRetrofit.api.deleteFile(
                DeleteFileRequest(cloudFile.publicId)
            )

            if (!response.isSuccessful) {

                return Result.failure(
                    Exception(
                        "Cloudinary delete failed : ${response.code()}"
                    )
                )

            }

            userFiles(cloudFile.ownerId)
                .document(cloudFile.id)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(TAG, "Delete failed", e)

            Result.failure(e)

        }

    }

    override suspend fun renameFile(
        cloudFile: CloudFile,
        newName: String
    ): Result<Unit> {

        return try {

            userFiles(cloudFile.ownerId)
                .document(cloudFile.id)
                .update(
                    "fileName",
                    newName
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(TAG, "Rename failed", e)

            Result.failure(e)

        }

    }

    override suspend fun setFavorite(
        cloudFile: CloudFile,
        isFavorite: Boolean
    ): Result<Unit> {

        return try {

            userFiles(cloudFile.ownerId)
                .document(cloudFile.id)
                .update(
                    "isFavorite",
                    isFavorite
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(TAG, "Favorite update failed", e)

            Result.failure(e)

        }

    }

    override suspend fun toggleFavorite(
        cloudFile: CloudFile
    ): Result<Unit> {

        return setFavorite(
            cloudFile,
            !cloudFile.isFavorite
        )

    }

    override suspend fun getFavoriteFiles(
        userId: String
    ): Result<List<CloudFile>> {

        return try {

            val snapshot = userFiles(userId)
                .whereEqualTo(
                    "isFavorite",
                    true
                )
                .orderBy(
                    "uploadedAt",
                    Query.Direction.DESCENDING
                )
                .get()
                .await()

            Result.success(
                snapshot.toObjects(CloudFile::class.java)
            )

        } catch (e: Exception) {

            Log.e(TAG, "Load favorites failed", e)

            Result.failure(e)

        }

    }

}