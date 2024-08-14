package com.adhibuchori.domain.repository.checkout

data class CheckoutModel(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val productStock: Int = 0,
    val productVariant: String = "",
    val productPrice: Int = 0,
    var productCount: Int = 1,
)