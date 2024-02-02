package com.tabka.backblogapp.util

// Standard return type for repository functions
sealed class DataResult<out T> {
    data class Failure(val throwable: Throwable) : DataResult<Nothing>()
    data class Success<T>(val item: T) : DataResult<T>()
}
