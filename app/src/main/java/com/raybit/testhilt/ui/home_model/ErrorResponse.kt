package com.raybit.testhilt.ui.home_model

data class ErrorResponse(
    val error: Error,
    val message: String,
    val stack: String,
    val status: String
)