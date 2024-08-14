package com.adhibuchori.data.auth.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: ProfileData? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ProfileData(

    @field:SerializedName("userImage")
    val userImage: String? = null,

    @field:SerializedName("userName")
    val userName: String? = null
)