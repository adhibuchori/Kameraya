package com.adhibuchori.domain.repository.wishlist

data class WishlistModel(
    val wishlistId: Int? = null,
    val productId: String = "",
    val productName: String = "",
    val productPrice: Int = 0,
    val productImage: String = "",
    val productStock: Int = 0,
    val productVariant: String = "",
    val productStore: String = "",
    val productReview: Double = 0.0,
)