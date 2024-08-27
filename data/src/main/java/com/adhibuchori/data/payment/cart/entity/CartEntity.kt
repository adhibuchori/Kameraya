package com.adhibuchori.data.payment.cart.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val cartId: Int? = null,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productStock: Int,
    val productVariant: String,
    val productPrice: Int,
    var productCount: Int = 1,
    val isChecked: Boolean,
)
