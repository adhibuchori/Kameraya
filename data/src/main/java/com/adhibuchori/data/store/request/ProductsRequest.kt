package com.adhibuchori.data.store.request

data class ProductsRequest(
    val search: String?,
    val brand: String?,
    val lowest: Int?,
    val highest: Int?,
    val sort: String?,
    val limit: Int?,
    val page: Int?
)