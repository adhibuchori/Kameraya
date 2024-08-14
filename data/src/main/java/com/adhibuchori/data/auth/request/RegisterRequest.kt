package com.adhibuchori.data.auth.request

data class RegisterRequest(
    val email: String?,
    val password: String?,
    val firebaseToken: String? = ""
)