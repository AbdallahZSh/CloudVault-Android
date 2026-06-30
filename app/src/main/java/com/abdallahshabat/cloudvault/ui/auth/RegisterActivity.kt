package com.abdallahshabat.cloudvault.ui.auth
/*
* getRegisterForm()
        │
        ▼
يجمع البيانات فقط

validateInputs()
        │
        ▼
يتحقق من صحة البيانات فقط

registerUser()
        │
        ▼
يستدعي ViewModel فقط */
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.abdallahshabat.cloudvault.core.validation.AuthValidator
import com.abdallahshabat.cloudvault.core.validation.ValidationResult
import com.abdallahshabat.cloudvault.data.model.RegisterForm
import com.abdallahshabat.cloudvault.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
/**
 * File Name : RegisterActivity.kt
 * Module    : Authentication
 * Project   : CloudVault
 * Handles the user registration screen.
 * This Activity is responsible only for UI interactions and
 * delegates business logic to the ViewModel.
 * مسؤول عن شاشة إنشاء الحساب.
 * يقتصر دوره على التعامل مع واجهة المستخدم فقط،
 * بينما يتم نقل منطق العمل إلى الـ ViewModel.
 */
class RegisterActivity : AppCompatActivity() {
    /**ViewModel responsible for authentication.
     * الـ ViewModel المسؤول عن عمليات المصادقة.
     */
    private val viewModel: AuthViewModel by viewModels()
    // English: Provides safe access to UI components.
    // العربية: يوفر وصولاً آمناً لعناصر الواجهة.
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupTextWatchers()
        initializeViews()
        setupClickListeners()
        observeViewModel()
    }

    /**
     * Initializes the screen state.
     * تهيئة الحالة الابتدائية للشاشة.
     */
    private fun initializeViews() {

        hideLoading()

    }

     // Registers all click listeners.
     // ربط جميع أحداث الضغط الخاصة بالمستخدم.
    private fun setupClickListeners() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

    }

    //Observes ViewModel states.
    // مراقبة الحالات القادمة من الـ ViewModel.
    private fun observeViewModel() {

        viewModel.authState.observe(this) { state ->

            when (state) {

                is AuthState.Idle -> {
                    hideLoading()
                }

                is AuthState.Loading -> {
                    showLoading()
                }

                is AuthState.Success -> {
                    hideLoading()

                    android.widget.Toast.makeText(
                        this,
                        "Registration successful",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                is AuthState.Error -> {
                    hideLoading()

                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()

                    viewModel.resetState()
                }
            }
        }
    }
    //Collects user input and starts registration.
    // يجمع بيانات المستخدم ويبدأ عملية إنشاء الحساب.
    private fun registerUser() {

        if (!validateInputs()) {
            return
        }

        val form = getRegisterForm()

        viewModel.register(
            form.fullName,
            form.email,
            form.password
        )
    }

   //Validates all registration fields using AuthValidator.
   //التحقق من جميع الحقول باستخدام AuthValidator.
    private fun validateInputs(): Boolean {

        clearErrors()

        val form = getRegisterForm()

        when (val result = AuthValidator.validateFullName(form.fullName)) {
            is ValidationResult.Invalid -> {
                showError(binding.tilName, binding.etName, result.message)
                return false
            }
            ValidationResult.Valid -> {}
        }

        when (val result = AuthValidator.validateEmail(form.email)) {
            is ValidationResult.Invalid -> {
                showError(binding.tilEmail, binding.etEmail, result.message)
                return false
            }
            ValidationResult.Valid -> {}
        }

        when (val result = AuthValidator.validatePassword(form.password)) {
            is ValidationResult.Invalid -> {
                showError(binding.tilPassword, binding.etPassword, result.message)
                return false
            }
            ValidationResult.Valid -> {}
        }

        when (
            val result = AuthValidator.validateConfirmPassword(
                form.password,
                form.confirmPassword
            )
        ) {
            is ValidationResult.Invalid -> {
                showError(
                    binding.tilConfirmPassword,
                    binding.etConfirmPassword,
                    result.message
                )
                return false
            }
            ValidationResult.Valid -> {}
        }

        return true
    }

    //Clears all validation errors.
    //إزالة جميع رسائل الخطأ من الحقول.
    private fun clearErrors() {

        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

    }

    //Displays an error message and focuses the corresponding field.
    // يعرض رسالة الخطأ ويضع المؤشر على الحقل المطلوب.
    private fun showError(
        layout: TextInputLayout,
        editText: TextInputEditText,
        message: String
    ) {

        layout.error = message
        editText.requestFocus()

    }


    // Clears validation errors while the user edits the fields.
    // إزالة رسائل الخطأ تلقائياً عند بدء المستخدم بتعديل الحقول.
    private fun setupTextWatchers() {

        binding.etName.doAfterTextChanged {
            binding.tilName.error = null
        }

        binding.etEmail.doAfterTextChanged {
            binding.tilEmail.error = null
        }

        binding.etPassword.doAfterTextChanged {
            binding.tilPassword.error = null
        }

        binding.etConfirmPassword.doAfterTextChanged {
            binding.tilConfirmPassword.error = null
        }

    }
    private fun showLoading() {

        binding.apply {
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false
        }

    }
    /**Hides loading state.
     * إخفاء مؤشر التحميل.*/
    private fun hideLoading() {

        binding.apply {
            progressBar.visibility = View.GONE
            btnRegister.isEnabled = true
        }

    }
    private fun getRegisterForm(): RegisterForm {

        return RegisterForm(
            fullName = binding.etName.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            password = binding.etPassword.text.toString(),
            confirmPassword = binding.etConfirmPassword.text.toString()
        )

    }
}