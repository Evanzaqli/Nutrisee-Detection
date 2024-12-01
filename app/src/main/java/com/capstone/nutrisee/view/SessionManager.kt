package com.capstone.nutrisee.view

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserSession(age: Int, height: Int, weight: Int, gender: String, goal: String) {
        val editor = sharedPreferences.edit()
        editor.putInt("age", age)
        editor.putInt("height", height)
        editor.putInt("weight", weight)
        editor.putString("gender", gender)
        editor.putString("goal", goal)
        editor.apply()
    }

    fun getUserSession(): Map<String, Any> {
        val age = sharedPreferences.getInt("age", -1)
        val height = sharedPreferences.getInt("height", -1)
        val weight = sharedPreferences.getInt("weight", -1)
        val gender = sharedPreferences.getString("gender", "") ?: ""
        val goal = sharedPreferences.getString("goal", "") ?: ""

        return mapOf(
            "age" to age,
            "height" to height,
            "weight" to weight,
            "gender" to gender,
            "goal" to goal
        )
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
