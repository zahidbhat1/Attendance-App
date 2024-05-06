package com.raybit.testhilt.ui.home_model

data class CheckInResponse(
    val `data`: Data,
    val status: String,
    val errorResponse: ErrorResponse
)