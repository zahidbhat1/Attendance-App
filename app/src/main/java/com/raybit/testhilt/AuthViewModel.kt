package com.raybit.testhilt

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.models.attendance.AttandanceModel
import com.raybit.testhilt.models.login_models.LoginResponse
import com.raybit.testhilt.models.login_models.SignInReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    val signInRes: LiveData<NetworkResult<LoginResponse>>
        get() = userRepository.signInRes
    private val _attendanceData = MutableLiveData<NetworkResult<AttandanceModel>>()
    val attendanceData: LiveData<NetworkResult<AttandanceModel>> = _attendanceData





    fun loginUser(signInReq: SignInReq) {
        viewModelScope.launch {
            userRepository.loginUser(signInReq)
        }
    }

    fun checkInUser() {
        viewModelScope.launch {
            userRepository.checkInUser()
            val result = userRepository.checkInUser()
            when (result) {
                is NetworkResult.Success -> {
                    Log.d("CheckInResponse", "Check-in successful: ${result.data}")
                }

                is NetworkResult.Error -> {
                    Log.e("CheckInResponse", "Check-in failed: ${result.message}")
                }

                is NetworkResult.Loading -> {
                    // Do nothing or handle loading state if necessary
                }
            }
        }
    }

    fun fetchAttendance() {
        viewModelScope.launch {
            _attendanceData.value = NetworkResult.Loading()
            _attendanceData.value = userRepository.getAttendance()
        }
    }





    fun validateCredentials(emailAddress: String, password: String, isLogin: Boolean): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (!isLogin && TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)) {
            result = Pair(false, "Please provide the credentials")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            result = Pair(false, "Please provide the valid credentials")
        } else if (password.length <= 5) {
            result = Pair(false, "Password length should be greater than 5")
        }
        return result
    }
}
