package com.capstone.nutrisee.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nutrisee.login.LoginActivity
import com.capstone.nutrisee.view.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Cek apakah pengguna sudah login
        if (isUserLoggedIn()) {
            navigateToHome()
        } else {
            navigateToLogin()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val token = sharedPreferences.getString("user_token", null)
        return token != null
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
