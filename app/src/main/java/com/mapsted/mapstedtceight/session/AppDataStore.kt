package com.mapsted.mapstedtceight.session

import android.content.Context
import javax.inject.Inject

class AppDataStore @Inject constructor(
    private val context: Context,
    private val appPreferences: AppPreferences,
    private val sessionPreferences: SessionPreferences,
) {

}