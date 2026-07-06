package com.abdallahshabat.cloudvault.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/*** ProfileViewModel
 * مسؤول عن جلب إحصائيات المستخدم (عدد الملفات، عدد الملفات المشتركة)
 * وأي منطق خاص بشاشة البروفايل، بعيدًا عن الـ Fragment (نفس نهج HomeViewModel).
 * Holds profile-related stats and exposes them via LiveData.*/
class ProfileViewModel : ViewModel() {

    // TODO: بدّل هاد بحقن الـ Repository الحقيقي عندك (نفس يلي مستخدم بـ HomeViewModel)
    // private val repository = FileRepository()

    private val _filesCount = MutableLiveData<Int>()
    val filesCount: LiveData<Int> get() = _filesCount

    private val _sharedCount = MutableLiveData<Int>()
    val sharedCount: LiveData<Int> get() = _sharedCount

    private val _statsError = MutableLiveData<String?>()
    val statsError: LiveData<String?> get() = _statsError

    private val _logoutState = MutableLiveData<Result<Unit>>()
    val logoutState: LiveData<Result<Unit>> get() = _logoutState

    /*** يجلب عدد الملفات وعدد الملفات المشتركة من Firestore
     * Loads files count + shared files count.*/
    fun loadStats() {
        viewModelScope.launch {
            try {
                // TODO: استبدلها باستدعاء حقيقي، مثال:
                // val files = repository.getUserFiles()
                // _filesCount.value = files.size
                // _sharedCount.value = files.count { it.isShared }

                _filesCount.value = 0
                _sharedCount.value = 0
            } catch (e: Exception) {
                _statsError.value = e.message ?: "حدث خطأ أثناء جلب الإحصائيات"
            }
        }
    }

    /*** ينفذ عملية تسجيل الخروج (مسح التوكن + أي تنظيف إضافي)
     * Performs logout logic beyond just clearing local session.*/
    fun logout() {
        viewModelScope.launch {
            try {
                // TODO: أي عملية إضافية (مثل إلغاء اشتراك Firestore listeners) قبل الخروج
                _logoutState.value = Result.success(Unit)
            } catch (e: Exception) {
                _logoutState.value = Result.failure(e)
            }
        }
    }
}