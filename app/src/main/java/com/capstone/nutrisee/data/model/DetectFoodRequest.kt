package com.capstone.nutrisee.data.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class DetectFoodRequest(
    val file: MultipartBody.Part
)


