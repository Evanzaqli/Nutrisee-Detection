package com.capstone.nutrisee.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutrisee.data.Result
import com.capstone.nutrisee.data.model.RegisterRequest
import com.capstone.nutrisee.data.model.RegisterResponse
import com.capstone.nutrisee.service.ApiConfig
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun register(username: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(username, email, password)

                val response = ApiConfig.getApiService().register(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _registerResult.value = Result.Success(body)
                    } else {
                        _registerResult.value = Result.Error("Registration failed: Response body is null")
                    }
                } else {
                    _registerResult.value = Result.Error("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _registerResult.value = Result.Error("Registration failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
