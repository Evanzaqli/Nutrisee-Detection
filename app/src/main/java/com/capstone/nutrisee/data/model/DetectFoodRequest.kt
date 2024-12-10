package com.capstone.nutrisee.data.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class DetectFoodRequest(
    val file: MultipartBody.Part
)

data class NutritionInfo(
    @SerializedName("class")
    val foodClass: String,

    @SerializedName("Calories (kcal)")
    val calories: Int,

    @SerializedName("Protein (g)")
    val protein: Double,

    @SerializedName("Carbohydrates (g)")
    val carbohydrates: Double,

    @SerializedName("Fat (g)")
    val fat: Double,

    @SerializedName("Fiber (g)")
    val fiber: Double
)
