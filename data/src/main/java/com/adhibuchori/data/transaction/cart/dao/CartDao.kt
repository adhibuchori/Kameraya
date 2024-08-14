package com.adhibuchori.data.transaction.cart.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adhibuchori.data.transaction.cart.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart where cartId = :id")
    suspend fun getCartById(id: Int): CartEntity?

    @Query("SELECT * FROM cart where productId = :productId AND productVariant = :productVariant")
    suspend fun getCartByIdAndVariant(productId: String, productVariant: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    @Delete
    suspend fun deleteCartItem(item: CartEntity)

    @Update
    suspend fun updateCartItem(item: CartEntity)
}