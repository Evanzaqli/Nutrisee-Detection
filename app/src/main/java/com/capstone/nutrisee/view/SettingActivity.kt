package com.capstone.nutrisee.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.nutrisee.R
import com.capstone.nutrisee.data.model.BmiDataResponse
import com.capstone.nutrisee.service.ApiConfig
import com.capstone.nutrisee.service.ApiService
import com.capstone.nutrisee.databinding.ActivitySettingBinding
import com.capstone.nutrisee.login.OnboardingActivity
import kotlinx.coroutines.launch
import retrofit2.Response

class SettingActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var binding: ActivitySettingBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiConfig.getApiService()
        sessionManager = SessionManager(this)


        binding.btnConfirm.setOnClickListener {
            val age = binding.editAge.text.toString().toIntOrNull()
            val height = binding.editHeight.text.toString().toIntOrNull()
            val weight = binding.editWeight.text.toString().toIntOrNull()

            val selectedGender = if (binding.radioFemale.isChecked) "Female" else "Male"
            val selectedGoal = when {
                binding.radioMaintain.isChecked -> "Maintain Weight"
                binding.radioLoss.isChecked -> "Lose Weight"
                binding.radioGain.isChecked -> "Gain Weight"
                else -> "Unknown"
            }


            if (age != null && height != null && weight != null) {
                sessionManager.saveUserSession(age, height, weight, selectedGender, selectedGoal)
            }

            getBmiData()
        }


        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()

            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    private fun getBmiData() {
        lifecycleScope.launch {
            try {
                val response: Response<List<BmiDataResponse>> = apiService.getBmiData()
                if (response.isSuccessful) {
                    val bmiData = response.body()
                    bmiData?.let {
                        // Proses data
                    }
                } else {
                    // Tangani jika gagal
                }
            } catch (e: Exception) {
                // Tangani jika terjadi error
            }
        }
    }
}

