package com.capstone.nutrisee.data.model

import com.google.gson.annotations.SerializedName

data class DetectFoodResponse(
    @SerializedName("detected_foods")
    val detectedFoods: List<String>,
    @SerializedName("nutrition_info")
    val nutritionInfo: List<NutritionInfo>
)

data class NutritionInfo(
    @SerializedName("class")
    val foodClass: String,
    @SerializedName("Calories (kcal)")
    val calories: Float,
    @SerializedName("Protein (g)")
    val protein: Float,
    @SerializedName("Carbohydrates (g)")
    val carbohydrates: Float,
    @SerializedName("Fat (g)")
    val fat: Float,
    @SerializedName("Fiber (g)")
    val fiber: Float
)

