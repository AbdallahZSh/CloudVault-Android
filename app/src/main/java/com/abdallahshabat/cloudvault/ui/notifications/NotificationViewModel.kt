package com.abdallahshabat.cloudvault.ui.notifications

import androidx.lifecycle.*
import com.abdallahshabat.cloudvault.data.model.NotificationItem
import com.abdallahshabat.cloudvault.data.repository.NotificationRepository
import com.abdallahshabat.cloudvault.data.repository.NotificationRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val repository: NotificationRepository = NotificationRepositoryImpl()

    private val _notifications = MutableLiveData<List<NotificationItem>>()

    val notifications: LiveData<List<NotificationItem>> = _notifications

    private val _unreadCount = MutableLiveData<Int>()

    val unreadCount: LiveData<Int> = _unreadCount

    fun loadNotifications() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getNotifications(uid).fold(

                    onSuccess = {

                        _notifications.value = it

                    },

                    onFailure = {

                        _notifications.value = emptyList()

                    }

                )

        }

    }

    fun loadUnreadCount() {

        val uid =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getUnreadCount(uid)
                .fold(

                    onSuccess = {

                        _unreadCount.value = it

                    },

                    onFailure = {

                        _unreadCount.value = 0

                    }

                )

        }

    }
    fun markAllAsRead() {

        val userId =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.markAllAsRead(userId)

            loadUnreadCount()

        }

    }
}