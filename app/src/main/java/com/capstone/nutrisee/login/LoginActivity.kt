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

        // Cek apakah token sudah ada di SharedPreferences, jika sudah langsung pindah ke MainActivity
        val token = getAuthToken()
        if (!token.isNullOrEmpty()) {
            Log.d("LoginActivity", "Token ditemukan, mengalihkan ke MainActivity")
            navigateToHome() // Pindah ke MainActivity jika token ada
            return
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginViewModel.login(email, password) // Memulai proses login
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

                // Menyimpan token ke SharedPreferences
                saveAuthToken(result.data.data.token)
                Log.d("LoginActivity", "Token berhasil disimpan: ${result.data.data.token}")

                // Verifikasi penyimpanan token
                val storedToken = getAuthToken()
                if (!storedToken.isNullOrEmpty()) {
                    Log.d("SharedPreferences", "Token berhasil disimpan: $storedToken")

                    // Cek apakah format token valid (misalnya, jika itu JWT, harus ada 3 bagian)
                    if (storedToken.split(".").size == 3) {
                        Log.d("SharedPreferences", "Format token valid.")
                    } else {
                        Log.d("SharedPreferences", "Format token tidak valid.")
                    }
                } else {
                    Log.d("SharedPreferences", "Token tidak ditemukan setelah disimpan.")
                }

                // Navigasi ke MainActivity setelah login berhasil
                navigateToHome()
            }
            is Result.Error -> {
                setLoadingState(false)
                showToast(result.error ?: getString(R.string.login_failed)) // Menampilkan pesan error jika login gagal
            }
        }
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
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
        finish() // Menutup LoginActivity setelah berhasil login
    }

    // Fungsi untuk menyimpan token di SharedPreferences
    private fun saveAuthToken(token: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
        Log.d("LoginActivity", "Token berhasil disimpan: $token")// Simpan token
    }

    // Fungsi untuk mendapatkan token dari SharedPreferences
    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null) // Mengambil token
        if (token == null) {
            Log.d("SharedPreferences", "Token tidak ditemukan")
        } else {
            Log.d("SharedPreferences", "Token ditemukan: $token")
        }
        return token
    }

}
