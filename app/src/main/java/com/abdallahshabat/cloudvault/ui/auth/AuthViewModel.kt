package com.abdallahshabat.cloudvault.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.data.model.User
import com.abdallahshabat.cloudvault.data.repository.AuthRepository
import com.abdallahshabat.cloudvault.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch

sealed class AuthState {

    object Idle : AuthState()

    object Loading : AuthState()

    data class Success(
        val user: User
    ) : AuthState()

    data class Error(
        val message: String
    ) : AuthState()

}

class AuthViewModel : ViewModel() {

    private val repository: AuthRepository = AuthRepositoryImpl()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    /**
     * إنشاء حساب جديد
     */
    fun register(
        fullName: String,
        email: String,
        password: String
    ) {

        _authState.value = AuthState.Loading

        viewModelScope.launch {

            repository.register(
                fullName,
                email,
                password
            ).fold(

                onSuccess = {

                    repository.getCurrentUser().fold(

                        onSuccess = { user ->

                            _authState.value = AuthState.Success(user)

                        },

                        onFailure = {

                            _authState.value = AuthState.Error(
                                it.message ?: "Failed to load user."
                            )

                        }

                    )

                },

                onFailure = {

                    _authState.value = AuthState.Error(
                        it.message ?: "Registration failed."
                    )

                }

            )

        }

    }

    /**
     * تسجيل الدخول
     */
    fun login(
        email: String,
        password: String
    ) {

        _authState.value = AuthState.Loading

        viewModelScope.launch {

            repository.login(
                email,
                password
            ).fold(

                onSuccess = {

                    repository.getCurrentUser().fold(

                        onSuccess = { user ->

                            _authState.value = AuthState.Success(user)

                        },

                        onFailure = {

                            _authState.value = AuthState.Error(
                                it.message ?: "Failed to load user."
                            )

                        }

                    )

                },

                onFailure = {

                    _authState.value = AuthState.Error(
                        it.message ?: "Login failed."
                    )

                }

            )

        }

    }

    /**
     * إعادة الحالة للوضع الابتدائي
     */
    fun resetState() {

        _authState.value = AuthState.Idle

    }
    fun logout() {

        repository.logout()

    }

}