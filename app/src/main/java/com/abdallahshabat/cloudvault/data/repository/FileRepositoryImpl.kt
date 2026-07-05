package com.abdallahshabat.cloudvault.data.repository

import android.content.Context
import android.net.Uri
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.remote.CloudinaryDataSource
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FileRepositoryImpl : FileRepository {
    private val cloudinaryDataSource = CloudinaryDataSource()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun uploadFile(
        context: Context,
        userId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long,
        listener: UploadProgressListener
    ): Result<Unit>  {

        return try {
            cloudinaryDataSource.uploadImage(
                context,
                fileUri,
                listener
            ).fold(
                onSuccess = { result ->

                    val document = firestore
                        .collection("users")
                        .document(userId)
                        .collection("files")
                        .document()

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
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getFiles(userId: String): Result<List<CloudFile>> {
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("files")
                .get()
                .await()

            val files = snapshot.toObjects(CloudFile::class.java)

            Result.success(files)

        } catch (exception: Exception) {

            Result.failure(exception)

        }

    }

    override suspend fun deleteFile(
        cloudFile: CloudFile
    ): Result<Unit> {

        return try {

            firestore
                .collection("users")
                .document(cloudFile.ownerId)
                .collection("files")
                .document(cloudFile.id)
                .delete()
                .await()

            Result.success(Unit)

        } catch (exception: Exception) {

            Result.failure(exception)
        }
    }

    //هذه الدالة تحدث فقط حقل:
    //
    //fileName
    //
    //ولا تعيد رفع الملف.
    override suspend fun renameFile(
        cloudFile: CloudFile,
        newName: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("users")
                .document(cloudFile.ownerId)
                .collection("files")
                .document(cloudFile.id)
                .update(
                    "fileName",
                    newName
                )
                .await()

            Result.success(Unit)

        } catch (exception: Exception) {

            Result.failure(exception)

        }

    }

}