package com.adhibuchori.domain.repository.wishlist.useCase

import android.util.Log
import com.adhibuchori.domain.repository.wishlist.IWishlistRepository
import com.adhibuchori.domain.repository.wishlist.WishlistModel

class RemoveWishlistITemUseCase(private val wishlistRepository: IWishlistRepository) {
    suspend operator fun invoke(data: WishlistModel) {
        val newModel = wishlistRepository.getWishlistByIdAndVariant(data.productId, data.productVariant)
        Log.d("RemoveUseCase", newModel.toString())
        newModel?.let {
            wishlistRepository.deleteWishlistItem(it)
        }
    }
}