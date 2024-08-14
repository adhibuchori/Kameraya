package com.adhibuchori.domain.repository.wishlist

import kotlinx.coroutines.flow.Flow

interface IWishlistRepository {
    fun getAllWishlistItems(): Flow<List<WishlistModel>>
    suspend fun getWishlistByIdAndVariant(wishlistId: String, productVariant: String): WishlistModel?
    suspend fun insertWishlistItem(item: WishlistModel)
    suspend fun deleteWishlistItem(item: WishlistModel)
}