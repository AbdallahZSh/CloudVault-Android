package com.abdallahshabat.cloudvault.data.remote.upload

interface UploadProgressListener {
    fun onProgressUpdate(percentage: Int)
}