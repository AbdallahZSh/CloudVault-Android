package com.abdallahshabat.cloudvault.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    private val _favoriteFiles = MutableLiveData<List<CloudFile>>()

    val favoriteFiles: LiveData<List<CloudFile>>
        get() = _favoriteFiles
    private val _favoriteState = MutableLiveData<Result<Unit>>()

    val favoriteState: LiveData<Result<Unit>>
        get() = _favoriteState

    fun loadFavorites() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getFavoriteFiles(userId).fold(

                onSuccess = {
                    Log.d("Favorites", "Files count = ${it.size}")

                    it.forEach { file ->
                        Log.d("Favorites", file.fileName + " favorite=" + file.isFavorite)
                    }

                    _favoriteFiles.value = it
                },

                onFailure = {
                    Log.e("Favorites", it.message ?: "Error")
                    _favoriteFiles.value = emptyList()
                }
            )
        }
    }
    fun toggleFavorite(file: CloudFile) {
        viewModelScope.launch {
            repository
                .setFavorite(file, !file.isFavorite).fold(
                    onSuccess = {
                        _favoriteState.value = Result.success(Unit)
                        loadFavorites()
                    },
                    onFailure = {
                        _favoriteState.value = Result.failure(it)
                    }
                )
        }
    }
}