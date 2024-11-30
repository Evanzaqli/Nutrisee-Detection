package com.capstone.nutrisee.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nutrisee.R
import com.capstone.nutrisee.login.LoginActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enable edge-to-edge UI
        setContentView(R.layout.activity_main)

        // Pengecekan token di SharedPreferences
        val token = getTokenFromSharedPreferences()


        // Menangani WindowInsets (untuk edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)  // Mengambil token yang tersimpan
    }

    // Fungsi untuk mengarahkan pengguna ke halaman login
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))  // Arahkan ke LoginActivity
        finish()  // Menutup MainActivity sehingga pengguna tidak bisa kembali
    }

}
