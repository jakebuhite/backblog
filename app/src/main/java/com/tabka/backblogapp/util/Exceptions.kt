package com.tabka.backblogapp.util

enum class FirebaseExceptionType {
    NOT_FOUND, FAILED_TRANSACTION, DOES_NOT_EXIST
}

enum class NetworkExceptionType {
    REQUEST_FAILED
}

class FirebaseError(val exceptionType: FirebaseExceptionType) : Exception()
class NetworkError(val exceptionType: NetworkExceptionType) : Exception()