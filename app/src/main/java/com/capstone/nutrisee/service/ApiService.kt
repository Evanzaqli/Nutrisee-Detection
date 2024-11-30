package com.capstone.nutrisee.service

import com.capstone.nutrisee.data.model.BmiDataResponse
import com.capstone.nutrisee.data.model.LoginRequest
import com.capstone.nutrisee.data.model.LoginResponse
import com.capstone.nutrisee.data.model.RegisterRequest
import com.capstone.nutrisee.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST(ApiConfig.LOGIN_ENDPOINT)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(ApiConfig.REGISTER_ENDPOINT)
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST(ApiConfig.BMI_DATA_ENDPOINT)
    suspend fun getBmiData(): Response<List<BmiDataResponse>>
}
