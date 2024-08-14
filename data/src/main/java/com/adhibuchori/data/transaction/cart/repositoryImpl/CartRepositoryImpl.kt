package com.adhibuchori.data.transaction.cart.repositoryImpl

import com.adhibuchori.data.transaction.cart.dao.CartDao
import com.adhibuchori.data.utils.toCartEntity
import com.adhibuchori.data.utils.toCartModel
import com.adhibuchori.data.utils.toCartProductList
import com.adhibuchori.domain.repository.cart.CartModel
import com.adhibuchori.domain.repository.cart.ICartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(private val cartDao: CartDao) : ICartRepository {

    override fun getAllCartItems(): Flow<List<CartModel>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.toCartProductList()
        }
    }

    override suspend fun getCartByIdAndVariant(
        productId: String,
        productVariant: String,
    ): CartModel? {
        return cartDao.getCartByIdAndVariant(productId, productVariant)?.toCartModel()
    }

    override suspend fun getCartById(id: Int): CartModel? {
        return cartDao.getCartById(id)?.toCartModel()
    }

    override suspend fun insertCartItem(item: CartModel) {
        cartDao.insertCartItem(item.toCartEntity())
    }

    override suspend fun deleteCartItem(item: CartModel) {
        cartDao.deleteCartItem(item.toCartEntity())
    }

    override suspend fun updateCartItem(item: CartModel) {
        cartDao.updateCartItem(item.toCartEntity())
    }
}