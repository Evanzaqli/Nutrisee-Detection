package com.capstone.nutrisee.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val BASE_URL = "https://node-service-dot-capstone-nutrisee-442807.et.r.appspot.com/"

        const val LOGIN_ENDPOINT = "${BASE_URL}auth/login"
        const val REGISTER_ENDPOINT = "${BASE_URL}auth/register"
        const val USER_DATA_ENDPOINT = "${BASE_URL}users/data"
        const val USER_DATA_DASHBOARD_ENDPOINT = "${BASE_URL}users/data/dashboard"


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
