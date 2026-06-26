package com.example.cloudapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.cloudapp.R
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudapp.databinding.ActivityLoginBinding
import com.example.cloudapp.ui.home.MainActivity
import com.example.cloudapp.utils.TokenManager
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeState()
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            // لاحقاً
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
        binding.btnLogin.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.alpha = if (isLoading) 0.7f else 1f
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.cv_danger))
            .setTextColor(getColor(android.R.color.white))
            .show()
    }
}