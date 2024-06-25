package com.mapsted.mapstedtceight.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.text.Selection
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import java.io.Serializable

fun AppCompatEditText.togglePassword(): Boolean {
    val isVisible = if (transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
        transformationMethod = HideReturnsTransformationMethod.getInstance()
        true
    } else {
        transformationMethod = PasswordTransformationMethod.getInstance()
        false
    }
    Selection.setSelection(text!!, length())
    return isVisible
}

fun AppCompatEditText.togglePassword(isVisible: Boolean) {
    transformationMethod = if (isVisible) {
        HideReturnsTransformationMethod.getInstance()
    } else {
        PasswordTransformationMethod.getInstance()
    }
    Selection.setSelection(text!!, length())
}

val statusBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() = with(Resources.getSystem()) {
        val resourceId: Int = getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

val navigationBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() = with(Resources.getSystem()) {
        val resourceId: Int = getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun String.clearHTML(): String {
    val value = this.trim()
    return if (value.isNotEmpty()) {
        Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT).toString().trim()
    } else {
        value
    }
}

fun String.asHtml(): Spanned {
    val value = this.trim()
    return if (value.isNotEmpty()) {
        Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
    } else {
        SpannableString.valueOf(value)
    }
}

inline fun <reified T : Serializable?> Intent.getSerializableData(key: String): T? {
    return kotlin.runCatching {
        if (hasExtra(key)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getSerializableExtra(key, T::class.java)
            } else {
                getSerializableExtra(key) as T?
            }
        } else {
            null
        }
    }.getOrNull()
}

fun Int.toDP() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPX() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.isInternetConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    cm.activeNetwork?.let { network ->
        cm.getNetworkCapabilities(network)?.let { cap ->
            if (cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                return true
            }
        }
    }
    return false
}

fun <K, V> Map<K, V>.get(key: K, default: V) = if (containsKey(key)) get(key) else default

fun Fragment.parentScreen(): Fragment? = parentFragment?.parentFragment

fun Window.setLightStatusBar(isLightStatusBar: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //SUPPORT after API 30
        insetsController?.apply {
            if (isLightStatusBar) {
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                setSystemBarsAppearance(0, 0)
            }
        }
    } else {
        //DEPRECATION after API 30
        @Suppress("DEPRECATION") decorView.apply {
            systemUiVisibility = if (isLightStatusBar) {
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}

fun String.append(string: String) = StringBuilder(this).append(string).toString()