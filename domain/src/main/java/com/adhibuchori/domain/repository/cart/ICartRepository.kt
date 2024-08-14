package com.adhibuchori.domain.repository.cart

import kotlinx.coroutines.flow.Flow

interface ICartRepository {
    fun getAllCartItems(): Flow<List<CartModel>>
    suspend fun getCartById(id: Int): CartModel?
    suspend fun getCartByIdAndVariant(productId: String, productVariant: String): CartModel?
    suspend fun insertCartItem(item: CartModel)
    suspend fun deleteCartItem(item: CartModel)
    suspend fun updateCartItem(item: CartModel)
}