package com.raybit.testhilt.api

import com.raybit.testhilt.Utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken = tokenManager.getToken()
        val originalRequest = chain.request()
        val modifiedRequest = if (authToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
        } else {
            originalRequest
        }
        return chain.proceed(modifiedRequest)
    }
}
