package com.abdallahshabat.cloudvault.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    // ---------------- Files ----------------

    private val _recentFiles = MutableLiveData<List<CloudFile>>()
    val recentFiles: LiveData<List<CloudFile>>
        get() = _recentFiles

    // ---------------- Delete ----------------

    private val _deleteState = MutableLiveData<Result<CloudFile>>()
    val deleteState: LiveData<Result<CloudFile>>
        get() = _deleteState

    // ---------------- Rename ----------------

    private val _renameState = MutableLiveData<Result<Unit>>()
    val renameState: LiveData<Result<Unit>>
        get() = _renameState

    // ---------------- Favorite ----------------

    private val _favoriteState = MutableLiveData<Result<Unit>>()
    val favoriteState: LiveData<Result<Unit>>
        get() = _favoriteState

    // ---------------- Load Files ----------------

    fun loadFiles() {

        val userId = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid ?: return

        viewModelScope.launch {

            repository.getFiles(userId)
                .fold(

                    onSuccess = {
                        _recentFiles.value = it
                    },

                    onFailure = {
                        _recentFiles.value = emptyList()
                    }

                )
        }
    }

    // ---------------- Delete ----------------

    fun deleteFile(file: CloudFile) {

        viewModelScope.launch {

            repository.deleteFile(file)
                .fold(

                    onSuccess = {
                        _deleteState.value = Result.success(file)
                    },

                    onFailure = {
                        _deleteState.value = Result.failure(it)
                    }

                )
        }
    }

    // ---------------- Rename ----------------

    fun renameFile(
        file: CloudFile,
        newName: String
    ) {

        viewModelScope.launch {

            repository.renameFile(file, newName)
                .fold(

                    onSuccess = {
                        _renameState.value = Result.success(Unit)
                        loadFiles()
                    },

                    onFailure = {
                        _renameState.value = Result.failure(it)
                    }

                )
        }
    }

    // ---------------- Favorite ----------------

    fun toggleFavorite(file: CloudFile) {

        viewModelScope.launch {

            repository.toggleFavorite(file)
                .fold(

                    onSuccess = {

                        _favoriteState.value = Result.success(Unit)

                        // Refresh RecyclerView
                        loadFiles()
                    },

                    onFailure = {

                        _favoriteState.value =
                            Result.failure(it)
                    }

                )
        }
    }

    /**
     * Optional
     * Keep this function if another screen
     * needs to set the value manually.
     */
    fun setFavorite(
        file: CloudFile,
        isFavorite: Boolean
    ) {

        viewModelScope.launch {

            repository.setFavorite(file, isFavorite)
                .fold(

                    onSuccess = {

                        _favoriteState.value =
                            Result.success(Unit)

                        loadFiles()
                    },

                    onFailure = {

                        _favoriteState.value =
                            Result.failure(it)
                    }

                )
        }
    }
}