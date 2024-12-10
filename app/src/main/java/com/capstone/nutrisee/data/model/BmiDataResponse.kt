package com.capstone.nutrisee.data.model

data class BmiRequest(
    val age: Int,
    val height: Double,
    val weight: Double,
    val gender: String
)

data class BmiDataResponse(
    val bmi: Double,
    val category: String,
    val carbohydrates: String,
    val protein: String,
    val fat: String,
    val fiber: String,
    val calories: String
)