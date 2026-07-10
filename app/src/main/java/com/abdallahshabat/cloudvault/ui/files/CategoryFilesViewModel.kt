package com.abdallahshabat.cloudvault.ui.files

import androidx.lifecycle.*
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CategoryFilesViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    private val _files = MutableLiveData<List<CloudFile>>()
    val files: LiveData<List<CloudFile>> = _files
    private val _favoriteState = MutableLiveData<Result<Unit>>()
    val favoriteState: LiveData<Result<Unit>> = _favoriteState

    fun toggleFavorite(file: CloudFile) {

        viewModelScope.launch {

            repository.setFavorite(file, !file.isFavorite).fold(

                onSuccess = {
                    _favoriteState.value = Result.success(Unit)
                    loadFiles(fileCategory)
                },

                onFailure = {
                    _favoriteState.value = Result.failure(it)
                }

            )

        }

    }

    private var fileCategory = ""

    fun loadFiles(category: String) {

        fileCategory = category

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getFiles(userId).fold(

                onSuccess = { list ->

                    _files.value = when (category) {

                        "Images" ->
                            list.filter {
                                it.fileType.startsWith("image")
                            }

                        "Videos" ->
                            list.filter {
                                it.fileType.startsWith("video")
                            }

                        "Audio" ->
                            list.filter {
                                it.fileType.startsWith("audio")
                            }

                        "Documents" ->
                            list.filter {

                                !it.fileType.startsWith("image") &&
                                        !it.fileType.startsWith("video") &&
                                        !it.fileType.startsWith("audio")

                            }

                        "DOC_PDF" ->
                            list.filter {

                                it.fileType.contains(
                                    "pdf",
                                    true
                                )

                            }

                        "DOC_Word" ->
                            list.filter {

                                it.fileName.endsWith(".doc", true) ||
                                        it.fileName.endsWith(".docx", true)

                            }

                        "DOC_Excel" ->
                            list.filter {

                                it.fileName.endsWith(".xls", true) ||
                                        it.fileName.endsWith(".xlsx", true)

                            }

                        "DOC_PowerPoint" ->
                            list.filter {

                                it.fileName.endsWith(".ppt", true) ||
                                        it.fileName.endsWith(".pptx", true)

                            }

                        "DOC_ZIP" ->
                            list.filter {

                                it.fileName.endsWith(".zip", true)

                            }

                        "DOC_Text" ->
                            list.filter {

                                it.fileName.endsWith(".txt", true)

                            }

                        else -> emptyList()

                    }

                },

                onFailure = {

                    _files.value = emptyList()

                }

            )

        }

    }

}