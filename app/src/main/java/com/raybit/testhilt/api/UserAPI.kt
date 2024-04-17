package com.raybit.testhilt.api

import com.raybit.testhilt.di.models.LoginResponse
import com.raybit.testhilt.di.models.SignInReq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserAPI {



        @POST("users/login")
        suspend fun signIn(
            @Body signInReq: SignInReq
        ): Response<LoginResponse>
    }

