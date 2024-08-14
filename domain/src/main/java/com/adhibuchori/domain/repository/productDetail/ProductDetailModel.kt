package com.adhibuchori.domain.repository.productDetail

data class ProductDetailModel(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Int = 0,
    val image: List<String> = emptyList(),
    val brand: String = "",
    val description: String = "",
    val store: String = "",
    val sale: Int = 0,
    val stock: Int = 0,
    val totalRating: Double = 0.0,
    val totalReview: Int = 0,
    val totalSatisfaction: Int = 0,
    val productRating: Double = 0.0,
    val productVariant: List<ProductVariant>,
)

data class ProductVariant(
    val variantName: String = "",
    val variantPrice: Int = 0,
)