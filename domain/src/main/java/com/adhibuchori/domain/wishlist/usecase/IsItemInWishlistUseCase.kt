package com.adhibuchori.domain.wishlist.usecase

import com.adhibuchori.domain.wishlist.IWishlistRepository
import kotlinx.coroutines.flow.first

open class IsItemInWishlistUseCase(private val wishlistRepository: IWishlistRepository) {
    suspend operator fun invoke(itemId: String, variantName: String): Boolean {
        val list = wishlistRepository.getAllWishlistItems().first()
        val isItemInWishlist = list.any {
            it.productId == itemId && it.productVariant == variantName
        }
        return isItemInWishlist
    }
}