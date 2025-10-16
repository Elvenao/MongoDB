package com.example.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
    val user: String? = null
)