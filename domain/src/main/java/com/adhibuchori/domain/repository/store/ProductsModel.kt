package com.adhibuchori.domain.repository.store

data class ProductsModel(
    val productId: String?,
    val productName: String?,
    val productPrice: Float?,
    val image: String?,
    val brand: String?,
    val store: String?,
    val sale: Float?,
    val productRating: Float?,
)