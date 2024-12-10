package com.capstone.nutrisee.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nutrisee.R
import com.capstone.nutrisee.data.Result
import com.capstone.nutrisee.databinding.ActivityRegisterBinding
import com.capstone.nutrisee.login.viewmodel.RegisterFactory
import com.capstone.nutrisee.register.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // Klik tombol register
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(username, email, password)) {
                registerViewModel.register(username, email, password)
            }
        }

        // Klik untuk kembali ke login
        binding.toLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun observeViewModel() {
        // Observasi loading state
        registerViewModel.isLoading.observe(this) { isLoading ->
            binding.btnRegister.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi hasil registrasi
        registerViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.btnRegister.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    // Menampilkan pesan sukses
                    Toast.makeText(this, R.string.pendaftaran_berhasil, Toast.LENGTH_SHORT).show()

                    // Navigasi ke LoginActivity setelah registrasi berhasil
                    navigateToLogin()
                }
                is Result.Error -> {
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    // Menampilkan pesan error
                    val errorMessage = result.error
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            binding.etUsername.error = getString(R.string.empty_username)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.empty_email)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.empty_password)
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = getString(R.string.password_too_short)
            isValid = false
        }

        return isValid
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}