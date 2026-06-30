package com.abdallahshabat.cloudvault.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.abdallahshabat.cloudvault.databinding.ActivityLoginBinding
import com.abdallahshabat.cloudvault.ui.home.MainActivity
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
    }

    private fun setupUI() {

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()

            val password = binding.etPassword.text.toString().trim()

            viewModel.login(
                email,
                password
            )

        }

        binding.tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )

        }

        binding.tvForgotPassword.setOnClickListener {
            // لاحقاً
        }

    }

    private fun observeState() {

        viewModel.authState.observe(this) { state ->

            when (state) {

                is AuthState.Idle -> {

                    setLoading(false)

                }

                is AuthState.Loading -> {

                    setLoading(true)

                }

                is AuthState.Success -> {

                    setLoading(false)

                    TokenManager.saveUser(
                        this,
                        state.user
                    )

                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )

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

        binding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnLogin.alpha =
            if (isLoading) 0.7f else 1f

    }

    private fun showError(message: String) {

        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        )
            .setBackgroundTint(
                getColor(R.color.cv_danger)
            )
            .setTextColor(
                getColor(android.R.color.white)
            )
            .show()

    }

}