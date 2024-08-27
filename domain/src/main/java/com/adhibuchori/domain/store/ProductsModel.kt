package com.adhibuchori.domain.store

data class ProductsModel(
    val productId: String?,
    val productName: String?,
    val productPrice: Int?,
    val image: String?,
    val brand: String?,
    val store: String?,
    val sale: Float?,
    val productRating: Float?,
)