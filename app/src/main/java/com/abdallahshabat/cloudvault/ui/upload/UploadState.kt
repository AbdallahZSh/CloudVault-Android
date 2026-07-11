package com.abdallahshabat.cloudvault.ui.upload

import com.abdallahshabat.cloudvault.data.model.CloudFile

sealed class UploadState {

    object Idle : UploadState()

    object Loading : UploadState()

    data class Success(
        val file: CloudFile
    ) : UploadState()


    data class Error(
        val message:String
    ) : UploadState()
}