package com.adhibuchori.domain.wishlist.usecase

import android.util.Log
import com.adhibuchori.domain.wishlist.IWishlistRepository
import com.adhibuchori.domain.wishlist.WishlistModel

open class RemoveWishlistITemUseCase(private val wishlistRepository: IWishlistRepository) {
    suspend operator fun invoke(data: WishlistModel) {
        val newModel = wishlistRepository.getWishlistByIdAndVariant(data.productId, data.productVariant)
        Log.d("RemoveUseCase", newModel.toString())
        newModel?.let {
            wishlistRepository.deleteWishlistItem(it)
        }
    }
}