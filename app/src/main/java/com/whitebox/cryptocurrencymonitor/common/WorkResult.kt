package com.whitebox.cryptocurrencymonitor.common

sealed class WorkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : WorkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : WorkResult<T>(data, message)
    class Loading<T>(data: T? = null) : WorkResult<T>(data)
}
