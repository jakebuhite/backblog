package com.tabka.backblogapp.util

sealed class DataResult<out T> {
    data class Failure(val throwable: Throwable) : DataResult<Nothing>()
    data class Success<T>(val item: T) : DataResult<T>()
}
