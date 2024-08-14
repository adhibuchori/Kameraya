package com.adhibuchori.domain.repository.cart

class AddCartUseCase(private val cartRepository: ICartRepository) {
    suspend operator fun invoke(data: CartModel) {
        val cartModel = cartRepository.getCartByIdAndVariant(data.productId, data.productVariant)
        cartModel?.let {
            val newQuantity = cartModel.productCount + 1
            val newModel = cartModel.copy(productCount = newQuantity)
            cartRepository.updateCartItem(newModel)
        } ?: cartRepository.insertCartItem(data)
    }
}