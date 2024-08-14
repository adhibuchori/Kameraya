package com.adhibuchori.domain.repository.wishlist.useCase

import com.adhibuchori.domain.repository.wishlist.IWishlistRepository
import com.adhibuchori.domain.repository.wishlist.WishlistModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class IsItemInWishlistUseCase(private val wishlistRepository: IWishlistRepository) {
    suspend operator fun invoke(itemId: String, variantName: String): Boolean {
        val list = wishlistRepository.getAllWishlistItems().first()
        val isItemInWishlist = list.any {
            it.productId == itemId && it.productVariant == variantName
        }
        return isItemInWishlist
    }
}