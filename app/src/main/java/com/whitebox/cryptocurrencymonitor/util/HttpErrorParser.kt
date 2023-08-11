package com.whitebox.cryptocurrencymonitor.util

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber
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
            Timber.tag("HttpErrorParser").e(e, "JsonParseException: ")
            null
        } catch (e: JsonSyntaxException) {
            Timber.tag("HttpErrorParser").e(e, "JsonSyntaxException: ")
            null
        }

    }

}