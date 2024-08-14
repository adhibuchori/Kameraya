package com.adhibuchori.data.auth.source

import com.adhibuchori.data.auth.request.LoginRequest
import com.adhibuchori.data.auth.request.RefreshRequest
import com.adhibuchori.data.auth.request.RegisterRequest
import com.adhibuchori.data.auth.response.LoginResponse
import com.adhibuchori.data.auth.response.ProfileResponse
import com.adhibuchori.data.auth.response.RefreshResponse
import com.adhibuchori.data.auth.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApiService {
    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ) : RegisterResponse

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : LoginResponse

    @Multipart
    @POST("profile")
    suspend fun profile(
        @Part("userName") userName: RequestBody,
        @Part userImage: MultipartBody.Part
    ): ProfileResponse

    @POST("refresh")
    suspend fun refresh(
        @Body token: RefreshRequest
    ) : RefreshResponse
}