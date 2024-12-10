package com.capstone.nutrisee.view

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "user_session"
        private const val TOKEN_KEY = "auth_token" // Key untuk menyimpan token autentikasi
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().apply {
            putString(TOKEN_KEY, token)
            apply()
            Log.d("SessionManager", "Token disimpan: $token")
        }
    }

    // Mengambil token autentikasi
    fun getAuthToken(): String? {
        val token = sharedPreferences.getString(TOKEN_KEY, null)
        Log.d("SessionManager", "Token diambil: $token")
        return token
    }

    // Menghapus token autentikasi
    fun clearAuthToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
        Log.d("SessionManager", "Token dihapus")
    }
}