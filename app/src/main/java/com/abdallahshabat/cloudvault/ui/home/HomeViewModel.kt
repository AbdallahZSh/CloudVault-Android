package com.abdallahshabat.cloudvault.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abdallahshabat.cloudvault.data.model.FileItem

class HomeViewModel : ViewModel() {

    private val _recentFiles = MutableLiveData<List<FileItem>>()
    val recentFiles: LiveData<List<FileItem>> = _recentFiles

    // سنضيف الـ API calls لاحقاً
}