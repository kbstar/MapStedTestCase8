package com.mapsted.mapstedtceight.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.mapsted.mapstedtceight.BuildConfig
import com.mapsted.mapstedtceight.session.AppDataStore
import com.mapsted.mapstedtceight.session.SessionPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkAPI @Inject constructor(private val baseUrl: String, private val context: Context, private val appDataStore: AppDataStore) {

    private val sessionPreferences = SessionPreferences(context)
    private val gson = GsonBuilder().serializeNulls().create()

    private val retrofit = Retrofit.Builder().apply {
        baseUrl(baseUrl)
        addConverterFactory(GsonConverterFactory.create(gson))
        client(OkHttpClient.Builder().apply {
            connectTimeout(2, TimeUnit.MINUTES)
            readTimeout(2, TimeUnit.MINUTES)
            writeTimeout(2, TimeUnit.MINUTES)
            callTimeout(2, TimeUnit.MINUTES)
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }.build())
    }.build()

    private fun isInternetConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.activeNetwork?.let { network ->
            cm.getNetworkCapabilities(network)?.let { cap ->
                if (cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    return true
                }
            }
        }
        return false
    }

    private fun handleFallback(e: Exception?, code: Int, errorResponse: String): RequestError {
        val statusCode: Int = if (e is HttpException) {
            e.code()
        } else {
            code
        }

        return when {
            e is ConnectException || e is UnknownHostException -> {
                //unable to connect to the host - Failed to Connect or No Internet Connection
                if (isInternetConnected()) {
                    //host unreachable
                    RequestError.HostUnreachable
                } else {
                    //no internet connection
                    RequestError.NoInternetConnection
                }
            }

            e is SocketTimeoutException -> {
                //connection timed out - Unable to connect with the server
                RequestError.ConnectionTimeout
            }

            statusCode == HttpURLConnection.HTTP_UNAUTHORIZED -> {
                //401 UNAUTHORIZED - Unauthorized Access
                RequestError.UnauthorizedAccess
            }

            statusCode in 500..599 -> {
                //500 to 599 server errors
                RequestError.ServerError(statusCode)
            }

            statusCode != 0 -> {
                RequestError.ResponseError(statusCode, errorResponse.trim())
            }

            e is MalformedJsonException || e is JsonIOException || e is JsonSyntaxException || e is JsonParseException -> {
                RequestError.ServerError(statusCode)
            }

            else -> {
                RequestError.UnknownError
            }
        }
    }

    private fun <T> Response<T>.handleCallback(): ResponseState<T> {
        return try {
            val statusCode = code()
            val responseBody = body()
            val errorBody = errorBody()
            if (statusCode in 200..299 && responseBody != null) {
                ResponseState.Success(responseBody)
            } else {
                val errorResponse = kotlin.runCatching {
                    errorBody?.string()
                }.getOrNull() ?: ""
                ResponseState.Error(handleFallback(null, statusCode, errorResponse))
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            ResponseState.Error(handleFallback(e, 0, ""))
        }
    }

    private fun <T> Result<Response<T>>.handleResult(): ResponseState<T> {
        getOrNull()?.let { response ->
            return response.handleCallback()
        } ?: kotlin.run {
            val e = Exception(exceptionOrNull())
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return ResponseState.Error(handleFallback(e, 0, ""))
        }
    }

    private val service = retrofit.create(NetworkApiService::class.java)

    //--------------------------------API IMPLEMENTATIONS--------------------------------

    suspend fun getBuildingData() = kotlin.runCatching {
        service.getBuildingData()
    }.handleResult()

    suspend fun getAnalyticData() = kotlin.runCatching {
        service.getAnalyticData()
    }.handleResult()
}