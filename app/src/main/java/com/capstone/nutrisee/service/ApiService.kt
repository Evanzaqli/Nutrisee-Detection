package com.capstone.nutrisee.service

import DashboardResponse
import com.capstone.nutrisee.data.model.DetectFoodResponse
import com.capstone.nutrisee.data.model.LoginRequest
import com.capstone.nutrisee.data.model.LoginResponse
import com.capstone.nutrisee.data.model.RegisterRequest
import com.capstone.nutrisee.data.model.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST(ApiConfig.REGISTER_ENDPOINT)
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("users/data/dashboard")
    suspend fun getDashboardData(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>

    @POST(ApiConfig.DETECT_FOOD_ENDPOINT)
    suspend fun detectFood(
        @Body request: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<DetectFoodResponse>
}