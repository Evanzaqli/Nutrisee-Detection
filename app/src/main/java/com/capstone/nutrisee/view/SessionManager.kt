package com.capstone.nutrisee.view

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "user_session"

        // Keys
        private const val AGE_KEY = "age"
        private const val HEIGHT_KEY = "height"
        private const val WEIGHT_KEY = "weight"
        private const val GENDER_KEY = "gender"
        private const val GOAL_KEY = "goal"
        private const val TOKEN_KEY = "auth_token"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Menyimpan data sesi pengguna
    fun saveUserSession(age: Int, height: Int, weight: Int, gender: String, goal: String) {
        sharedPreferences.edit().apply {
            putInt(AGE_KEY, age)
            putInt(HEIGHT_KEY, height)
            putInt(WEIGHT_KEY, weight)
            putString(GENDER_KEY, gender)
            putString(GOAL_KEY, goal)
            apply()
        }
    }

    // Mengambil data sesi pengguna
    fun getUserSession(): Map<String, Any?> {
        return mapOf(
            "age" to sharedPreferences.getInt(AGE_KEY, -1),
            "height" to sharedPreferences.getInt(HEIGHT_KEY, -1),
            "weight" to sharedPreferences.getInt(WEIGHT_KEY, -1),
            "gender" to sharedPreferences.getString(GENDER_KEY, null),
            "goal" to sharedPreferences.getString(GOAL_KEY, null)
        )
    }

    // Menghapus data sesi pengguna
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }

    // Menyimpan auth token
    fun saveAuthToken(token: String) {
        sharedPreferences.edit().apply {
            putString(TOKEN_KEY, token)
            apply()
            Log.d("SessionManager", "Token disimpan: $token")
        }
    }

    // Mengambil auth token
    fun getAuthToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    // Menghapus auth token
    fun clearAuthToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }
}
