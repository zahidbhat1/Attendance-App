package com.raybit.testhilt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.api.UserAPI
import com.raybit.testhilt.di.models.LoginResponse
import com.raybit.testhilt.di.models.SignInReq
import org.json.JSONObject
import javax.inject.Inject


class UserRepository  @Inject constructor(private val userAPI: UserAPI) {


    private val _signInRes = MutableLiveData<NetworkResult<LoginResponse>>()
    val signInRes: LiveData<NetworkResult<LoginResponse>>
        get() = _signInRes
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: MutableLiveData<String?>
        get() = _imageUrl


    suspend fun loginUser(signInReq: SignInReq){
        _signInRes.value=NetworkResult.Loading()
        val response= userAPI.signIn(signInReq)
        if (response.isSuccessful && response.body() != null) {
            _signInRes.value = NetworkResult.Success(response.body()!!)
            val imageUrl = response.body()?.data?.profile?.image
            _imageUrl.postValue(imageUrl)


            Log.d("zahid",response.body().toString())





        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _signInRes.value = NetworkResult.Error(errorObj.getString("message"))
        } else {
            _signInRes.value = NetworkResult.Error("Something is Wrong")
        }
    }



}