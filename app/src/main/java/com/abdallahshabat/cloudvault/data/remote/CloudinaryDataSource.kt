package com.abdallahshabat.cloudvault.data.remote

import android.content.Context
import android.net.Uri
import com.abdallahshabat.cloudvault.data.model.UploadResult
import com.abdallahshabat.cloudvault.data.network.CloudinaryRetrofit
import com.abdallahshabat.cloudvault.data.remote.upload.ProgressRequestBody
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CloudinaryDataSource {

    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        listener: UploadProgressListener
    ): Result<UploadResult>{

        return try {

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Cannot open file"))

            val tempFile = File.createTempFile("upload", ".tmp", context.cacheDir)

            tempFile.outputStream().use {
                inputStream.copyTo(it)
            }

            val requestBody = ProgressRequestBody(
                tempFile,
                "*/*".toMediaTypeOrNull(),
                listener
            )

            val filePart = MultipartBody.Part.createFormData(
                "file",
                tempFile.name,
                requestBody
            )

            val preset =
                "cloudvault_upload"
                    .toRequestBody("text/plain".toMediaTypeOrNull())

            val response = CloudinaryRetrofit.api.uploadImage(
                "muydjqhs",
                filePart,
                preset
            )

            return Result.success(
                UploadResult(
                    fileUrl = response.secureUrl,
                    publicId = response.publicId
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}