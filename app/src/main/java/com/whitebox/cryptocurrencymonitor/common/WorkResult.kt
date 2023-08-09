package com.whitebox.cryptocurrencymonitor.common

/**
 * A generic sealed class that holds the state or result of an
 * operation (network or cache).
 *
 * @param data some data of any type
 * @param message a message to be displayed in case of error
 */
sealed class WorkResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : WorkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : WorkResult<T>(data, message)
    class Loading<T>(data: T? = null) : WorkResult<T>(data)
}
