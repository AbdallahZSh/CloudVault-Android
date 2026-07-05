package com.abdallahshabat.cloudvault.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE

interface DeleteApi {

    @DELETE("api/files")
    suspend fun deleteFile(
        @Body request: DeleteFileRequest
    ): Response<Unit>

}