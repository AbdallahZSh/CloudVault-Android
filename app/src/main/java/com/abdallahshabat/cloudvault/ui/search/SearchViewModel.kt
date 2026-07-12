package com.abdallahshabat.cloudvault.ui.search

import androidx.lifecycle.*
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    private val _results = MutableLiveData<List<CloudFile>>()

    val results: LiveData<List<CloudFile>> = _results
    private var currentFilter = "ALL"

    private var allFiles = listOf<CloudFile>()

    private var currentQuery = ""

    private var currentSort = "NEWEST"
    fun search(query: String) {

        currentQuery = query

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            repository.searchFiles(userId, query).fold(
                onSuccess = {
                    allFiles = it
                    applyFilter()
                },
                onFailure = {
                    _results.value = emptyList()
                }
            )
        }
    }
    fun setFilter(filter: String) {

        currentFilter = filter

        applyFilter()

    }
    private fun applyFilter() {

        val filtered = when (currentFilter) {

            "IMAGE" -> allFiles.filter {
                    it.fileType.startsWith("image")
                }

            "VIDEO" -> allFiles.filter {
                    it.fileType.startsWith("video")
                }

            "AUDIO" -> allFiles.filter {
                    it.fileType.startsWith("audio")
                }

            "DOCUMENT" -> allFiles.filter {
                    !it.fileType.startsWith("image") &&
                            !it.fileType.startsWith("video") &&
                            !it.fileType.startsWith("audio")

                }
            else -> allFiles
        }
        val sorted = when (currentSort) {
            "NEWEST" -> filtered.sortedByDescending { it.uploadedAt }
            "OLDEST" -> filtered.sortedBy { it.uploadedAt }
            "LARGEST" -> filtered.sortedByDescending { it.fileSize }
            "SMALLEST" -> filtered.sortedBy { it.fileSize }
            "AZ" -> filtered.sortedBy { it.fileName.lowercase() }
            "ZA" -> filtered.sortedByDescending { it.fileName.lowercase() }
            else -> filtered
        }
        _results.value = sorted
    }

    fun setSort(sort: String) {

        currentSort = sort

        applyFilter()

    }
}