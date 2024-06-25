package com.mapsted.mapstedtceight.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

typealias LayoutInflate<T> = (LayoutInflater) -> T

typealias ViewInflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

typealias ItemInflate = (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding

typealias OnItemClick<T> = (T, position: Int) -> Unit

fun <T> T?.defaultValue(default: T) = this ?: default