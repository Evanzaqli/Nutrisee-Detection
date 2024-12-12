package com.capstone.nutrisee.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        private const val BASE_URL = "https://node-service-dot-capstone-nutrisee-442807.et.r.appspot.com/"
        private const val DETECT_FOOD_BASE_URL = "https://model-1-api-dot-capstone-nutrisee-442807.et.r.appspot.com/"

        const val LOGIN_ENDPOINT = "${BASE_URL}auth/login"
        const val REGISTER_ENDPOINT = "${BASE_URL}auth/register"
        const val USER_DATA_ENDPOINT = "${BASE_URL}users/data"
        const val USER_DATA_DASHBOARD_ENDPOINT = "${BASE_URL}users/data/dashboard"
        const val DETECT_FOOD_ENDPOINT = "${DETECT_FOOD_BASE_URL}detect_food"

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

        fun getDetectFoodApiService(): ApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(DETECT_FOOD_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}
