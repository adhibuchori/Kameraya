package com.adhibuchori.data.wishlist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.adhibuchori.data.transaction.cart.entity.CartEntity
import com.adhibuchori.data.wishlist.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist")
    fun getAllWishlistItems(): Flow<List<WishlistEntity>>

    @Query("SELECT * FROM wishlist where productId = :productId AND productVariant = :productVariant")
    suspend fun getWishlistByIdAndVariant(productId: String, productVariant: String): WishlistEntity?

    @Insert
    suspend fun insertWishlistItem(item: WishlistEntity)

    @Delete
    suspend fun deleteWishlistItem(item: WishlistEntity)
}