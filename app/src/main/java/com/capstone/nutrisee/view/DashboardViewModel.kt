package com.capstone.nutrisee.view

import NutritionHistory
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutrisee.service.ApiService
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardViewModel(private val context: Context) : ViewModel() {

    private val _nutritionHistory = MutableLiveData<NutritionHistory?>()
    val nutritionHistory: LiveData<NutritionHistory?> = _nutritionHistory

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://node-service-dot-capstone-nutrisee-442807.et.r.appspot.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        startPollingData()
    }

    private fun startPollingData() {
        viewModelScope.launch {
            while (true) {
                val token = getAuthToken()
                if (token != null) {
                    fetchDashboardData(token)
                } else {
                    Log.e("DashboardViewModel", "Token tidak tersedia")
                }
                delay(5000) // Delay 10 detik sebelum memanggil API lagi
            }
        }
    }

    private fun getAuthToken(): String? {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    fun fetchDashboardData(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getDashboardData("Bearer $token")

                if (response.isSuccessful) {
                    val nutritionHistoryData = response.body()?.data?.nutritionHistory
                    if (nutritionHistoryData != _nutritionHistory.value) {
                        _nutritionHistory.postValue(nutritionHistoryData)
                        Log.d("DashboardViewModel", "Data berhasil diambil: $nutritionHistoryData")
                    }
                } else {
                    Log.e("DashboardViewModel", "Gagal mendapatkan data. Status: ${response.code()}")
                    if (response.code() == 401) {
                        Log.e("DashboardViewModel", "Token mungkin telah kedaluwarsa.")
                    }
                    _nutritionHistory.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Kesalahan jaringan: ${e.message}", e)
                _nutritionHistory.postValue(null)
            }
        }
    }
}
