package com.mapsted.mapstedtceight.network

interface RequestError {
    object NoInternetConnection : RequestError
    object ConnectionTimeout : RequestError
    object HostUnreachable : RequestError
    object UnauthorizedAccess : RequestError
    data class ResponseError(val statusCode: Int, val errorMessage: String) : RequestError
    data class ServerError(val statusCode: Int) : RequestError
    object UnknownError : RequestError
}