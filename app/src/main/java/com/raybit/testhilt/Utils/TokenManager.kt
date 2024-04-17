package com.raybit.testhilt.Utils

import android.content.Context
import android.util.Log
import com.raybit.testhilt.Utils.Constants.PREFS_TOKEN_FILE
import com.raybit.testhilt.Utils.Constants.TAG
import com.raybit.testhilt.Utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_TOKEN_FILE,Context.MODE_PRIVATE)

    fun saveToken(token:String){
        val editor= prefs.edit()
        editor.putString(USER_TOKEN,token)
        editor.apply()
        Log.d("zahid", "Token saved: $token")
    }

    fun getToken() : String? {
        return prefs.getString(USER_TOKEN,null)

    }



}