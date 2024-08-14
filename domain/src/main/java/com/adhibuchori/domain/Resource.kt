package com.adhibuchori.domain

sealed class Resource<out R> private constructor() {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String?) : Resource<Nothing>()
    data class HttpError(val errorCode: Int, val message: String?) : Resource<Nothing>()
    data object NetworkError : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}