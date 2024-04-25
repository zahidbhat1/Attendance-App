package com.raybit.testhilt.models.attendance

data class ErrorResponse(
    val error: Error,
    val message: String,
    val stack: String,
    val status: String
)