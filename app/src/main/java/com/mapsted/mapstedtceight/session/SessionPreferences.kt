package com.mapsted.mapstedtceight.session

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SessionPreferences @Inject constructor(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("SessionData", Context.MODE_PRIVATE)

    companion object {
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
    }

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    var accessToken: String
        set(value) = preferences.edit().putString(ACCESS_TOKEN, value.trim()).apply()
        get() = preferences.getString(ACCESS_TOKEN, "") ?: ""

    val bearerToken: String
        get() = "Bearer $accessToken"
}