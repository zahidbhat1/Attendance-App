package com.raybit.testhilt.ui.home_model

data class Error(
    val message: String,
    val name: String,
    val status: String,
    val statusCode: Int
)