package com.capstone.nutrisee.data.model

import com.google.gson.annotations.SerializedName

data class DetectFoodResponse(
    @SerializedName("detected_foods")
    val detectedFoods: List<String>,
    @SerializedName("nutrition_info")
    val nutritionInfo: List<NutritionInfo>
)
