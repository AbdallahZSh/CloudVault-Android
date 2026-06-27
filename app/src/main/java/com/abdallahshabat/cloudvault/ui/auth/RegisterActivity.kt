package com.abdallahshabat.cloudvault.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.abdallahshabat.cloudvault.ui.home.MainActivity
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.example.cloudapp.R
import com.example.cloudapp.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeState()
    }

    private fun setupUI() {
        // زر التسجيل
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirm = binding.etConfirmPassword.text.toString().trim()

            if (password != confirm) {
                showError("كلمتا المرور غير متطابقتين")
                return@setOnClickListener
            }
            viewModel.register(name, email, password)
        }

        // الرجوع للـ Login
        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun observeState() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Idle -> setLoading(false)

                is AuthState.Loading -> setLoading(true)

                is AuthState.Success -> {
                    setLoading(false)
                    TokenManager.saveToken(this, state.data.token)
                    TokenManager.saveUser(this, state.data.user)
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }

                is AuthState.Error -> {
                    setLoading(false)
                    showError(state.message)
                    viewModel.resetState()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.alpha = if (isLoading) 0.7f else 1f
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.cv_danger))
            .setTextColor(getColor(android.R.color.white))
            .show()
    }
}