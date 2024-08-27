package com.adhibuchori.data.productDetail.response

data class ProductReviewResponse(
    val code: Int? = null,
    val data: List<DataItem?>? = null,
    val message: String? = null,
)

data class DataItem(
    val userImage: String? = null,
    val userName: String? = null,
    val userReview: String? = null,
    val userRating: Double? = null,
)