package com.mapsted.mapstedtceight.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.mapsted.mapstedtceight.BuildConfig
import com.mapsted.mapstedtceight.session.AppDataStore
import com.mapsted.mapstedtceight.session.SessionPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
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
            addInterceptor(provideNullKeysInterceptor())
        }.build())
    }.build()

    private fun provideNullKeysInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response: okhttp3.Response = chain.proceed(chain.request())
            try {
                if (response.isSuccessful) {
                    val responseBody = response.body
                    val stringResponse = responseBody.string()
                    var element = gson.fromJson(stringResponse, JsonElement::class.java)

                    if (element.isJsonObject) {
                        element = removeNullKeysFromJsonObject(element.asJsonObject)
                        if (BuildConfig.DEBUG) {
                            println("RetroFit Interceptor Original Response: $element")
                            println("RetroFit Interceptor: Removed keys that has null value from response")
                        }

                        val returnBody = element.toString().toResponseBody(responseBody.contentType())

                        return@Interceptor response.newBuilder()
                            .body(returnBody)
                            .build()
                    } else if (element.isJsonArray) {
                        element = removeNullKeysFromJsonArray(element.asJsonArray)
                        if (BuildConfig.DEBUG) {
                            println("RetroFit Interceptor Original Response: $element")
                            println("RetroFit Interceptor: Removed keys that has null value from response")
                        }

                        val returnBody = element.toString().toResponseBody(responseBody.contentType())

                        return@Interceptor response.newBuilder()
                            .body(returnBody)
                            .build()
                    } else {
                        return@Interceptor response
                    }
                } else {
                    return@Interceptor response
                }
            } catch (e: Exception) {
                return@Interceptor response
            }
        }
    }

    private fun removeNullKeysFromJsonObject(jsonObject: JsonObject): JsonObject {
        val result = jsonObject.deepCopy()
        jsonObject.entrySet()?.forEach { entry ->
            entry.key?.let { key ->
                entry.value?.let { element ->
                    if (element.isJsonNull) {
                        result.remove(key)
                    } else if (element.isJsonObject) {
                        result.add(key, removeNullKeysFromJsonObject(element.asJsonObject))
                    } else if (element.isJsonArray) {
                        result.add(key, removeNullKeysFromJsonArray(element.asJsonArray))
                    }
                }
            }
        }
        return result
    }

    private fun removeNullKeysFromJsonArray(jsonArray: JsonArray): JsonArray {
        val result = jsonArray.deepCopy()
        jsonArray.forEachIndexed { index, element ->
            if (element.isJsonNull) {
                result.remove(index)
            } else if (element.isJsonObject) {
                result[index] = removeNullKeysFromJsonObject(element.asJsonObject)
            } else if (element.isJsonArray) {
                result[index] = removeNullKeysFromJsonArray(element.asJsonArray)
            }
        }
        return result
    }

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
                /*
                val errorMessage: String = try {
                    Gson().fromJson(errorResponse, JsonObject::class.java).get("message").asString ?: ""
                } catch (e: Exception) {
                    ""
                }
                RequestError.ResponseError(statusCode, errorMessage.trim())
                */
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

    suspend fun login(uid: String) = kotlin.runCatching {
        service.login(
            uid = uid
        )
    }.handleResult()

    suspend fun getCities() = kotlin.runCatching {
        service.getCities(
            token = sessionPreferences.bearerToken
        )
    }.handleResult()
}