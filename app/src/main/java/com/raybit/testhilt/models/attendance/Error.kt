package com.raybit.testhilt.models.attendance

data class Error(
    val message: String,
    val name: String,
    val status: String,
    val statusCode: Int
)