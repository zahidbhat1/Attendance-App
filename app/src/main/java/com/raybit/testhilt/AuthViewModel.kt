package com.raybit.testhilt

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.di.models.LoginResponse
import com.raybit.testhilt.di.models.SignInReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    val imageUrl: MutableLiveData<String?>
        get() = userRepository.imageUrl

    val signInRes: LiveData<NetworkResult<LoginResponse>>
        get() = userRepository.signInRes

    fun loginUser(signInReq: SignInReq) {
        viewModelScope.launch {
            userRepository.loginUser(signInReq)
        }
    }




    fun validateCredentials( emailAddress: String, password: String, isLogin: Boolean): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (!isLogin &&  TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)) {
            result = Pair(false, "Please provide the credentials")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            result = Pair(false, "Please provide the valid credentials")
        } else if (password.length <= 5) {
            result = Pair(false, "Password length should be greater than 5")
        }
        return result
    }
}
