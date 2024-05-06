package com.raybit.testhilt.Utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.raybit.testhilt.Utils.Constants.PREFS_TOKEN_FILE
import com.raybit.testhilt.Utils.Constants.PREF_CHECKED_IN_KEY
import com.raybit.testhilt.Utils.Constants.TAG
import com.raybit.testhilt.Utils.Constants.USER_TOKEN
import com.raybit.testhilt.models.login_models.Data
import com.raybit.testhilt.models.login_models.Profile
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

    fun saveProfile(profile: Profile) {
        val profileJson = Gson().toJson(profile)
        prefs.edit().putString(USER_PROFILE, profileJson).apply()
    }

    fun getProfile(): Profile? {
        val profileJson = prefs.getString(USER_PROFILE, null)
        return Gson().fromJson(profileJson, Profile::class.java)
    }


    fun saveCheckoutStatus(userId: String, isCheckedOut: Boolean) {
        val editor = prefs.edit()
        val key = getUserCheckoutStatusKey(userId)
        editor.putBoolean(key, isCheckedOut)
        editor.apply()
        Log.d(TAG, "Checkout status saved for user $userId: $isCheckedOut")
    }

    fun getCheckoutStatus(userId: String): Boolean {
        val key = getUserCheckoutStatusKey(userId)
        return prefs.getBoolean(key, false)
    }

    fun saveCheckinStatus(userId: String, isCheckedIn: Boolean) {
        val editor = prefs.edit()
        val key = getUserCheckinStatusKey(userId)
        editor.putBoolean(key, isCheckedIn)
        editor.apply()
        Log.d(TAG, "Check-in status saved for user $userId: $isCheckedIn")
    }

    fun getCheckinStatus(userId: String): Boolean {
        val key = getUserCheckinStatusKey(userId)
        return prefs.getBoolean(key, false)
    }

    fun clearStatus(userId: String) {
        val editor = prefs.edit()
        val checkoutKey = getUserCheckoutStatusKey(userId)
        val checkinKey = getUserCheckinStatusKey(userId)
        editor.remove(checkoutKey)
        editor.remove(checkinKey)
        editor.apply()
        Log.d(TAG, "Status cleared for user $userId")
    }

    private fun getUserCheckoutStatusKey(userId: String): String {
        return "$userId-$USER_CHECKOUT_STATUS"
    }

    private fun getUserCheckinStatusKey(userId: String): String {
        return "$userId-$PREF_CHECKED_IN_KEY"
    }

    companion object {
        private const val USER_PROFILE = "USER_PROFILE"
        private const val USER_CHECKOUT_STATUS = "USER_CHECKOUT_STATUS"
    }
}


