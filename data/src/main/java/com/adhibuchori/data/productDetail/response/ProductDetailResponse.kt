package com.adhibuchori.data.productDetail.response

data class ProductDetailResponse(
    val code: Int? = null,
    val data: Data? = null,
    val message: String? = null,
)

data class ProductVariantItem(
    val variantPrice: Int? = null,
    val variantName: String? = null,
)

data class Data(
    val image: List<String?>? = null,
    val productId: String? = null,
    val description: String? = null,
    val totalRating: Double? = null,
    val store: String? = null,
    val productName: String? = null,
    val totalSatisfaction: Int? = null,
    val sale: Int? = null,
    val productVariant: List<ProductVariantItem?>? = null,
    val stock: Int? = null,
    val productRating: Double? = null,
    val brand: String? = null,
    val productPrice: Int? = null,
    val totalReview: Int? = null,
)