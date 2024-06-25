package com.mapsted.mapstedtceight.utils

import androidx.activity.OnBackPressedCallback

class OnBackPressed(
    private val callback: () -> Unit = {}
) : OnBackPressedCallback(true) {

    override fun handleOnBackPressed() {
        callback.invoke()
    }
}