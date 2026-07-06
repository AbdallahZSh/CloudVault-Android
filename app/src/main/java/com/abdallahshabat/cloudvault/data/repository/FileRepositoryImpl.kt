package com.abdallahshabat.cloudvault.data.repository

import android.content.Context
import android.net.Uri
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.remote.CloudinaryDataSource
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.abdallahshabat.cloudvault.data.network.DeleteFileRequest
import com.abdallahshabat.cloudvault.data.network.DeleteRetrofit

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

    override suspend fun deleteFile(cloudFile: CloudFile): Result<Unit> {
        //Android
        //     ▼
        //Backend API
        //     ▼
        //Cloudinary
        //     ▼
        //Firestore
        //أي:
        //يحذف الملف من Cloudinary.
        //إذا نجح الحذف، يحذف سجل Firestore.
        //إذا فشل الحذف من Cloudinary، فلن يحذف Firestore، وبذلك لن يبقى رابط لملف غير موجود.
        return try {

            val response = DeleteRetrofit.api.deleteFile(
                DeleteFileRequest(
                    publicId = cloudFile.publicId
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("Failed to delete file from Cloudinary")
                )
            }

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