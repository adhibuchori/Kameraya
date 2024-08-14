package com.adhibuchori.data.auth.request

data class LoginRequest (
    val email: String?,
    val password: String?,
    val firebaseToken: String? = ""
)