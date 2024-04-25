package com.raybit.testhilt.models.login_models

data class LoginResponse(
    val `data`: Data,
    val message: String,
    val status: String,
    val error: Error

)