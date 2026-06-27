package com.abdallahshabat.cloudvault.ui.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.data.model.AuthResponse
import com.abdallahshabat.cloudvault.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    fun login(email: String, password: String) {
        if (!validateLogin(email, password)) return

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    _authState.value = AuthState.Success(response.body()!!)
                } else {
                    _authState.value = AuthState.Error("البريد أو كلمة المرور غلط")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("تحقق من اتصال الإنترنت")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (!validateRegister(name, email, password)) return

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                if (response.isSuccessful && response.body() != null) {
                    _authState.value = AuthState.Success(response.body()!!)
                } else {
                    _authState.value = AuthState.Error("حصل خطأ، حاول مرة ثانية")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("تحقق من اتصال الإنترنت")
            }
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("أدخل البيانات كاملة")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("البريد الإلكتروني غير صحيح")
            return false
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("كلمة المرور أقل من 6 أحرف")
            return false
        }
        return true
    }

    private fun validateRegister(name: String, email: String, password: String): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("أدخل البيانات كاملة")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("البريد الإلكتروني غير صحيح")
            return false
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("كلمة المرور يجب أن تكون 6 أحرف على الأقل")
            return false
        }
        return true
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}