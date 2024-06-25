package com.mapsted.mapstedtceight.network

sealed class ApiCallState<T> {
    class Idle<T> : ApiCallState<T>()
    class Loading<T> : ApiCallState<T>()
    data class Success<T>(val response: T) : ApiCallState<T>()
    data class Error<T>(val error: RequestError) : ApiCallState<T>()
}