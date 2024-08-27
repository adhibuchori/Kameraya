package com.adhibuchori.domain.wishlist.usecase

import com.adhibuchori.domain.wishlist.IWishlistRepository
import com.adhibuchori.domain.wishlist.WishlistModel

open class AddWishlistItemUseCase(private val wishlistRepository: IWishlistRepository) {
    suspend operator fun invoke(data: WishlistModel) {
        wishlistRepository.insertWishlistItem(data)
    }
}