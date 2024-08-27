package com.adhibuchori.data.payment.fullfilment.source

import com.adhibuchori.data.payment.fullfilment.request.FulfillmentRequest
import com.adhibuchori.data.payment.fullfilment.request.RatingRequest
import com.adhibuchori.data.payment.fullfilment.response.FulfillmentResponse
import com.adhibuchori.data.payment.fullfilment.response.RatingResponse
import com.adhibuchori.data.payment.fullfilment.response.TransactionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TransactionApiService {
    @POST("fulfillment")
    suspend fun fulfillTransaction(@Body request: FulfillmentRequest): FulfillmentResponse

    @POST("rating")
    suspend fun submitRating(@Body request: RatingRequest): RatingResponse

    @GET("transaction")
    suspend fun getTransaction(): TransactionResponse
}