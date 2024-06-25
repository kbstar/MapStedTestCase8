package com.mapsted.mapstedtceight.session

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AppPreferences @Inject constructor(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)

    companion object {
        const val KEY_APP_VERSION_NAME = "KEY_APP_VERSION_NAME"
        const val KEY_APP_VERSION_CODE = "KEY_APP_VERSION_CODE"
        const val KEY_APP_LANGUAGE_CODE = "KEY_APP_LANGUAGE_CODE"
    }

    var appVersionName: String
        set(value) = preferences.edit().putString(KEY_APP_VERSION_NAME, value).apply()
        get() = preferences.getString(KEY_APP_VERSION_NAME, "") ?: ""

    var appVersionCode: Int
        set(value) = preferences.edit().putInt(KEY_APP_VERSION_CODE, value).apply()
        get() = preferences.getInt(KEY_APP_VERSION_CODE, 0)

    fun setAppLanguageCode(value: String) {
        preferences.edit().putString(KEY_APP_LANGUAGE_CODE, value).apply()
    }

    fun getAppLanguageCode(): String {
        return preferences.getString(KEY_APP_LANGUAGE_CODE, "") ?: ""
    }
}
