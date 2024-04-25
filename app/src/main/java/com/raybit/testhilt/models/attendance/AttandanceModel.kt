package com.raybit.testhilt.models.attendance

data class AttandanceModel(
    val `data`: Data,
    val status: String,
    val errorResponse: ErrorResponse
)