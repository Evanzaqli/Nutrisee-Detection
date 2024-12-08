package com.capstone.nutrisee.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nutrisee.R
import com.capstone.nutrisee.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Memeriksa token saat aplikasi dibuka
        val token = getAuthToken()
        Log.d("MainActivity", "Token yang diterima: $token")

        if (token.isNullOrEmpty()) {
            // Token tidak ada, arahkan ke halaman login
            navigateToLogin()
            return
        }

        // Set up the rest of your MainActivity UI components
        setupImageLaunchers()

        // Menangani WindowInsets (untuk edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menangani klik pada BottomNavigationView
        findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.button_camera -> {
                    showImageSourceDialog()
                    true
                }
                R.id.button_home -> {
                    true
                }
                R.id.button_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // Navigasi ke halaman login
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java)) // Arahkan ke LoginActivity
        finish() // Menutup MainActivity sehingga pengguna tidak bisa kembali
    }

    // Setup image launchers atau komponen lain yang diperlukan
    private fun setupImageLaunchers() {
        // Setup your image launchers here
    }

    // Menampilkan dialog untuk memilih antara kamera dan galeri
    private fun showImageSourceDialog() {
        // Show dialog to choose between camera and gallery
    }

    // Mengambil token dari SharedPreferences
    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null) // Mengambil token, jika tidak ada akan mengembalikan null
    }

    // Menyimpan token login ke SharedPreferences
    private fun saveAuthToken(token: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token) // Menyimpan token
        editor.apply() // Simpan secara asynchronous
    }

    // Menghapus token saat logout
    private fun clearAuthToken() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token") // Menghapus token
        editor.apply()
    }
}
