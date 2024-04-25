package com.raybit.testhilt.models.login_models

data class Error(
    val isOperational: Boolean,
    val status: String,
    val statusCode: Int
)