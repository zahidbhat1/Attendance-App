package com.raybit.testhilt.di.models

data class Error(
    val isOperational: Boolean,
    val status: String,
    val statusCode: Int
)