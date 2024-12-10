package com.capstone.nutrisee.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutrisee.data.Result
import com.capstone.nutrisee.data.model.LoginRequest
import com.capstone.nutrisee.data.model.LoginResponse
import com.capstone.nutrisee.service.ApiConfig
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk hasil login
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    // Fungsi login yang akan dipanggil dari Activity
    fun login(email: String, password: String) {
        _isLoading.value = true // Menandakan loading dimulai
        viewModelScope.launch {
            try {
                // Panggil API login dengan email dan password langsung
                val response = ApiConfig.getApiService().login(LoginRequest(email, password))
                Log.d("LoginActivity", "Response Code: ${response.code()}")
                Log.d("LoginActivity", "Response Message: ${response.message()}")
                Log.d("LoginActivity", "Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data.token != null) {
                        Log.d("LoginActivity", "Token: ${body.data.token}")
                        _loginResult.value = Result.Success(body)
                        // Kirimkan token ke Activity untuk disimpan
                    } else {
                        Log.e("LoginActivity", "Login failed: Token is null")
                        _loginResult.value = Result.Error("Login failed: Token is null")
                    }
                } else {
                    Log.e("LoginActivity", "Login failed: ${response.message()}")
                    _loginResult.value = Result.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                // Menangani error jaringan atau lainnya
                _loginResult.value = Result.Error("Login failed: ${e.message}")
            } finally {
                _isLoading.value = false // Menandakan loading selesai
            }
        }
    }
}
