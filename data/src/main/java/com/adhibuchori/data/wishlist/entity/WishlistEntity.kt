package com.adhibuchori.data.wishlist.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true) val wishlistId: Int? = null,
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val productImage: String,
    val productStock: Int,
    val productVariant: String,
    val productStore: String,
    val productReview: Double,
)