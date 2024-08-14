package com.adhibuchori.data.productDetail.source

import com.adhibuchori.data.productDetail.response.ProductDetailResponse
import com.adhibuchori.data.productDetail.response.ProductReviewResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductDetailApiService {
    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") productId: String,
    ): ProductDetailResponse

    @GET("review/{id}")
    suspend fun getProductReviews(
        @Path("id") id: String,
    ): ProductReviewResponse
}