package com.abdallahshabat.cloudvault.ui.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.data.remote.upload.UploadProgressListener
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import kotlinx.coroutines.launch

class UploadViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    private val _uploadState =
        MutableLiveData<UploadState>(UploadState.Idle)

    val uploadState: LiveData<UploadState>
        get() = _uploadState
    private val _uploadProgress = MutableLiveData<Int>()
    val uploadProgress: LiveData<Int> = _uploadProgress

    fun uploadFile(
        context: Context,
        userId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long
    ) {

        _uploadState.value = UploadState.Loading

        viewModelScope.launch {

            repository.uploadFile(
                context,
                userId,
                fileName,
                fileUri,
                fileType,
                fileSize,
                object : UploadProgressListener {

                    override fun onProgressUpdate(
                        percentage: Int
                    ) {
                        _uploadProgress.postValue(percentage)
                    }

                }
            ).fold(

                onSuccess = {
                    _uploadState.value = UploadState.Success
                },

                onFailure = {
                    _uploadState.value =
                        UploadState.Error(it.message ?: "Upload failed")
                }
            )
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }

}