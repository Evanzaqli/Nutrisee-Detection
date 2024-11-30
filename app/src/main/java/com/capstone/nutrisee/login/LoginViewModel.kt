package com.capstone.nutrisee.login

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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Panggil API login
                val response = ApiConfig.getApiService().login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _loginResult.value = Result.Success(body)
                    } else {
                        _loginResult.value = Result.Error("Login failed: Response body is null")
                    }
                } else {
                    _loginResult.value = Result.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error("Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
