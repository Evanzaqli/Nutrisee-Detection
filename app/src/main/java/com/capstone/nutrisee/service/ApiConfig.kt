package com.capstone.nutrisee.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val BASE_URL = "https://bangkit-capstone-442321.uc.r.appspot.com/auth/"

        // Konstanta untuk setiap endpoint API
        const val LOGIN_ENDPOINT = "${BASE_URL}login"
        const val REGISTER_ENDPOINT = "${BASE_URL}register"
        const val BMI_DATA_ENDPOINT = "${BASE_URL}bmi"

        fun getApiService(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
