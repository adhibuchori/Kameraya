package com.adhibuchori.data.store.source

import com.adhibuchori.data.store.response.ProductsResponse
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface StoreApiService {
    @POST("products")
    suspend fun products(
        @QueryMap queryMap: Map<String, String>
    ): ProductsResponse
}