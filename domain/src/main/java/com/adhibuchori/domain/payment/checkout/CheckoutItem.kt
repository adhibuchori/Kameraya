package com.adhibuchori.domain.payment.checkout

import android.os.Parcelable
import com.adhibuchori.domain.payment.cart.CartModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckoutItem(
    val cartItems: List<CartModel>? = emptyList()
) : Parcelable