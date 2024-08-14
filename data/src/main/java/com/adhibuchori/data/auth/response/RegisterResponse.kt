package com.adhibuchori.data.auth.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: RegisterData? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class RegisterData(

    @field:SerializedName("accessToken")
    val accessToken: String? = null,

    @field:SerializedName("expiresAt")
    val expiresAt: Int? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null
)