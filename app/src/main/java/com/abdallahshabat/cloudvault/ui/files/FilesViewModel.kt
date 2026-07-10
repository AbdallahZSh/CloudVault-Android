package com.abdallahshabat.cloudvault.ui.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.model.FileCategory
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FilesViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    private val _categories = MutableLiveData<List<FileCategory>>()
    val categories: LiveData<List<FileCategory>> = _categories

    private val _files = MutableLiveData<List<CloudFile>>()
    val files: LiveData<List<CloudFile>> = _files

    private val _totalFiles = MutableLiveData<Int>()
    val totalFiles: LiveData<Int> = _totalFiles

    private val _totalSize = MutableLiveData<Long>()
    val totalSize: LiveData<Long> = _totalSize

    fun loadFiles() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getFiles(userId).fold(

                onSuccess = { files ->

                    _files.value = files
                    _totalFiles.value = files.size

                    _totalSize.value = files.sumOf {
                        it.fileSize
                    }

                    updateCategories(files)

                },

                onFailure = {

                    _files.value = emptyList()

                    _categories.value = emptyList()

                }

            )

        }

    }
    private fun updateCategories(files: List<CloudFile>) {

        val images = files.count {
            it.fileType.startsWith("image")
        }

        val videos = files.count {
            it.fileType.startsWith("video")
        }

        val audio = files.count {
            it.fileType.startsWith("audio")
        }

        val documents = files.count {

            !it.fileType.startsWith("image") &&
                    !it.fileType.startsWith("video") &&
                    !it.fileType.startsWith("audio")

        }

        _categories.value = listOf(

            FileCategory(
                "Images",
                R.drawable.ic_images,
                images
            ),

            FileCategory(
                "Videos",
                R.drawable.ic_video,
                videos
            ),

            FileCategory(
                "Audio",
                R.drawable.ic_audio,
                audio
            ),

            FileCategory(
                "Documents",
                R.drawable.ic_document,
                documents
            )

        )

    }

}