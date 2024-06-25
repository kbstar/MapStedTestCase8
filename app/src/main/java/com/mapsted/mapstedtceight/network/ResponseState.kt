package com.mapsted.mapstedtceight.network

sealed class ResponseState<T> {
    data class Success<T>(val response: T) : ResponseState<T>()
    data class Error<T>(val error: RequestError) : ResponseState<T>()
}