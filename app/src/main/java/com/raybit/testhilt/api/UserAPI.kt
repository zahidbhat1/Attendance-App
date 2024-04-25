package com.raybit.testhilt.api

import com.raybit.testhilt.models.attendance.AttandanceModel
import com.raybit.testhilt.models.attendance.Attendance
import com.raybit.testhilt.models.login_models.LoginResponse
import com.raybit.testhilt.models.login_models.SignInReq
import com.raybit.testhilt.ui.home_model.CheckInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserAPI {



        @POST("users/login")
        suspend fun signIn(
            @Body signInReq: SignInReq
        ): Response<LoginResponse>
    @POST("attendance/checkin")
    suspend fun checkInUser(
        @Header("Authorization") authToken: String
    ): Response<CheckInResponse>

    @GET("attendance/getattendance")
    suspend fun getAttendance(
        @Header("Authorization") authToken: String
    ): Response<AttandanceModel>


    }


