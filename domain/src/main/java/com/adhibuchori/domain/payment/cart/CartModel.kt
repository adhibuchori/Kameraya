package com.adhibuchori.domain.payment.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartModel(
    val cartId: Int? = null,
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val productStock: Int = 0,
    val productVariant: String = "",
    val productPrice: Int = 0,
    var productCount: Int = 1,
    val isChecked: Boolean = false,
) : Parcelable