package com.adhibuchori.data.auth.response

import com.google.gson.annotations.SerializedName

data class RefreshResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: RefreshData? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class RefreshData(

    @field:SerializedName("accessToken")
    val accessToken: String? = null,

    @field:SerializedName("expiresAt")
    val expiresAt: Int? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null
)
