package com.raybit.testhilt.di.models

data class LoginResponse(
    val `data`: Data,
    val message: String,
    val status: String,
    val error: Error

)