package com.raybit.testhilt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.Utils.TokenManager
import com.raybit.testhilt.api.UserAPI
import com.raybit.testhilt.models.attendance.AttandanceModel
import com.raybit.testhilt.models.login_models.LoginResponse
import com.raybit.testhilt.models.login_models.SignInReq
import com.raybit.testhilt.ui.home_model.CheckInResponse
import org.json.JSONObject
import javax.inject.Inject


class UserRepository  @Inject constructor(private val userAPI: UserAPI, private val tokenManager: TokenManager) {


    private val _signInRes = MutableLiveData<NetworkResult<LoginResponse>>()
    val signInRes: LiveData<NetworkResult<LoginResponse>>
        get() = _signInRes
    private val _attendanceData = MutableLiveData<NetworkResult<List<AttandanceModel>>>()
    val attendanceData: LiveData<NetworkResult<List<AttandanceModel>>> = _attendanceData


    suspend fun loginUser(signInReq: SignInReq) {
        _signInRes.value = NetworkResult.Loading()
        val response = userAPI.signIn(signInReq)
        if (response.isSuccessful && response.body() != null) {
            _signInRes.value = NetworkResult.Success(response.body()!!)



            Log.d("zahid", response.body().toString())


        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _signInRes.value = NetworkResult.Error(errorObj.getString("message"))
        } else {
            _signInRes.value = NetworkResult.Error("Something is Wrong")
        }
    }



    suspend fun checkInUser(): NetworkResult<CheckInResponse> {
        val token = tokenManager.getToken()
        return try {
            if (!token.isNullOrEmpty()) {
                val response = userAPI.checkInUser("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val checkInResponse = response.body()!!
                    Log.d("UserRepository", "Check-in successful. Response: $checkInResponse")

                    Log.d("zahid", "Check-in response: $checkInResponse")
                    NetworkResult.Success(checkInResponse)
                } else {
                    val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                    NetworkResult.Error(errorObj.getString("message"))
                }
            } else {
                NetworkResult.Error("Token not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Check-in failed: ${e.message}")
        }

    }
    suspend fun getAttendance(): NetworkResult<AttandanceModel> {
        val token = tokenManager.getToken()
        return try {
            if (!token.isNullOrEmpty()) {
                val response = userAPI.getAttendance("Bearer $token")
                if (response.isSuccessful) {
                    val attendanceResponse = response.body()
                    attendanceResponse?.let {
                        Log.d("AttendanceRepository", "Attendance fetched successfully. Response: $attendanceResponse")
                        NetworkResult.Success(it)
                    } ?: NetworkResult.Error("Attendance data is null")
                } else {
                    val errorObj = JSONObject(response.errorBody()?.charStream()?.readText() ?: "")
                    NetworkResult.Error(errorObj.getString("message"))
                }
            } else {
                NetworkResult.Error("Token not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Failed to fetch attendance: ${e.message}")
        }
    }



}