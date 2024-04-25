package com.raybit.testhilt.models.attendance

data class Attendance(
    val __v: Int,
    val _id: String,
    val breakId: List<Any>,
    val checkinTime: String,
    val checkoutTime: String,
    val createdAt: String,
    val isApproved: String,
    val updatedAt: String,
    val userId: UserId
)