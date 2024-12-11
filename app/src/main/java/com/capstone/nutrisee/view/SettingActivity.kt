package com.capstone.nutrisee.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nutrisee.databinding.ActivitySettingBinding
import com.capstone.nutrisee.login.LoginActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil token dari SharedPreferences
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            Log.e("SettingActivity", "Token tidak ditemukan, mengarahkan ke login")
            redirectToLogin()
        } else {
            Log.d("SettingActivity", "Token ditemukan: $token")
        }

        binding.btnConfirm.setOnClickListener {
            sendUserDataToApi(token)
        }

        binding.btnLogout.setOnClickListener {
            clearAuthToken()
            redirectToLogin()
        }
    }

    private fun sendUserDataToApi(token: String?) {
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable tombol untuk mencegah klik berulang
        binding.btnConfirm.isEnabled = false

        // Data pengguna yang akan dikirim
        val age = 25
        val gender = "male"
        val height = 170
        val weight = 70
        val targetWeight = "maintain weight"

        // Log setiap field yang akan dikirim
        Log.d("SettingActivity", "Preparing data to send:")
        Log.d("SettingActivity", "Age: $age")
        Log.d("SettingActivity", "Gender: $gender")
        Log.d("SettingActivity", "Height: $height")
        Log.d("SettingActivity", "Weight: $weight")
        Log.d("SettingActivity", "Target Weight: $targetWeight")

        // Membuat JSON Body
        val jsonBody = JSONObject()
        jsonBody.put("age", age)
        jsonBody.put("gender", gender)
        jsonBody.put("height", height)
        jsonBody.put("weight", weight)
        jsonBody.put("targetWeight", targetWeight)

        // Log JSON Body yang akan dikirim
        Log.d("SettingActivity", "JSON Body: ${jsonBody.toString()}")

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        val request = Request.Builder()
            .url("https://node-service-dot-capstone-nutrisee-442807.et.r.appspot.com/users/data")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SettingActivity", "Request failed", e)
                runOnUiThread {
                    binding.btnConfirm.isEnabled = true
                    Toast.makeText(this@SettingActivity, "Gagal mengirim data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.btnConfirm.isEnabled = true
                    if (response.isSuccessful) {
                        Log.d("SettingActivity", "Data berhasil dikirim: ${response.body?.string()}")
                        Toast.makeText(this@SettingActivity, "Data berhasil dikirim", Toast.LENGTH_SHORT).show()

                        // Pindah ke MainActivity setelah sukses
                        navigateToMainActivity()
                    } else {
                        Log.e("SettingActivity", "Gagal mengirim data: ${response.code}")
                        Log.e("SettingActivity", "Response body: ${response.body?.string()}")
                        Toast.makeText(this@SettingActivity, "Gagal mengirim data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // Menambahkan fungsi navigateToMainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Menutup SettingActivity setelah berpindah
    }

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

    private fun clearAuthToken() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token").apply()
        Log.d("SettingActivity", "Token berhasil dihapus")
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
