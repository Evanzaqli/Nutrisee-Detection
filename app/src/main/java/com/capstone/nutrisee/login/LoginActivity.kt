package com.capstone.nutrisee.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nutrisee.R
import com.capstone.nutrisee.data.Result
import com.capstone.nutrisee.data.model.LoginResponse
import com.capstone.nutrisee.databinding.ActivityLoginBinding
import com.capstone.nutrisee.view.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginViewModel.login(email, password)
            }
        }

        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun observeViewModel() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            setLoadingState(isLoading)
        }

        loginViewModel.loginResult.observe(this) { result ->
            handleLoginResult(result)
        }
    }

    private fun handleLoginResult(result: Result<LoginResponse>) {
        when (result) {
            is Result.Loading -> setLoadingState(true)
            is Result.Success -> {
                setLoadingState(false)
                showToast("Login berhasil")
                // Menyimpan token di SharedPreferences setelah login sukses
                saveTokenToSharedPreferences(result.data.token)
                navigateToHome()
            }
            is Result.Error -> {
                setLoadingState(false)
                showToast(result.error ?: getString(R.string.login_failed))
            }
        }
    }
    private fun saveTokenToSharedPreferences(token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)  // Menyimpan token
        editor.apply()
    }


    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.empty_email)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.empty_password)
            isValid = false
        }

        return isValid
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        Log.d("LoginActivity", "Navigating to MainActivity")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
