package com.whitebox.cryptocurrencymonitor.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

interface HttpErrorParser {
    fun parseResponseBody(errorBody: String?): ErrorResponse?
}

data class ErrorResponse(
    val error: String? = null
)

class HttpErrorParserImpl @Inject constructor(
    private val gson: Gson
) : HttpErrorParser {
    override fun parseResponseBody(errorBody: String?): ErrorResponse? {
        return try {
            val type = object : TypeToken<ErrorResponse>() {}.type
            gson.fromJson(errorBody, type)
        } catch (e: JsonParseException) {
            Log.e("HttpErrorParser", "JsonParseException: ", e)
            null
        } catch (e: JsonSyntaxException) {
            Log.e("HttpErrorParser", "JsonSyntaxException: ", e)
            null
        }

    }

}