package com.adhibuchori.domain.repository.wishlist.useCase

import com.adhibuchori.domain.repository.wishlist.IWishlistRepository
import com.adhibuchori.domain.repository.wishlist.WishlistModel

class AddWishlistItemUseCase(private val wishlistRepository: IWishlistRepository) {
    suspend operator fun invoke(data: WishlistModel) {
        wishlistRepository.insertWishlistItem(data)
    }
}