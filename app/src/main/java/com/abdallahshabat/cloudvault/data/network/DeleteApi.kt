package com.abdallahshabat.cloudvault.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP

interface DeleteApi {
    @HTTP(
        method = "DELETE",
        path = "api/files",
        hasBody = true
    )
    suspend fun deleteFile(
        @Body request: DeleteFileRequest
    ): Response<Unit>

}