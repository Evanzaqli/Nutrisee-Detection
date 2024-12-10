package com.capstone.nutrisee.data.model

data class LoginResponse(
    val status: String,
    val message: String,
    val data :Data

)
data class Data(
    val token: String
)


