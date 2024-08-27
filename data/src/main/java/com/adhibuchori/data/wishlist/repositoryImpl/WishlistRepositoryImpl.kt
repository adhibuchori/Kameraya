package com.adhibuchori.data.wishlist.repositoryImpl

import com.adhibuchori.data.utils.toWishlistEntity
import com.adhibuchori.data.utils.toWishlistProductList
import com.adhibuchori.data.utils.toWishlistModel
import com.adhibuchori.data.wishlist.dao.WishlistDao
import com.adhibuchori.domain.wishlist.IWishlistRepository
import com.adhibuchori.domain.wishlist.WishlistModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WishlistRepositoryImpl(private val wishlistDao: WishlistDao) : IWishlistRepository {

    override fun getAllWishlistItems(): Flow<List<WishlistModel>> {
        return wishlistDao.getAllWishlistItems().map { entities ->
            entities.toWishlistProductList()
        }
    }

    override suspend fun getWishlistByIdAndVariant(
        wishlistId: String,
        productVariant: String
    ): WishlistModel? {
        return wishlistDao.getWishlistByIdAndVariant(wishlistId, productVariant)?.toWishlistModel()
    }

    override suspend fun insertWishlistItem(item: WishlistModel) {
        wishlistDao.insertWishlistItem(item.toWishlistEntity())
    }

    override suspend fun deleteWishlistItem(item: WishlistModel) {
        wishlistDao.deleteWishlistItem(item.toWishlistEntity())
    }
}