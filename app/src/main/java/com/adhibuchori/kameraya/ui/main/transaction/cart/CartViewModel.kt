package com.adhibuchori.kameraya.ui.main.transaction.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.domain.repository.cart.CartModel
import com.adhibuchori.domain.repository.cart.ICartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: ICartRepository) : ViewModel() {

    val cartItems = cartRepository.getAllCartItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var isAllItemSelectEligible = true

    fun addCartItem(item: CartModel) {
        viewModelScope.launch {
            cartRepository.insertCartItem(item)
        }
    }

    fun removeCartItem(item: CartModel) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(item)
        }
    }

    private fun updateCartItem(item: CartModel) {
        viewModelScope.launch {
            cartRepository.updateCartItem(item)
        }
    }

    fun handleOnItemChecked(status: Boolean, item: CartModel) {
        val newModel = cartItems.value.find { it.cartId == item.cartId }
        newModel?.let {
            updateCartItem(it.copy(isChecked = status))
        }
    }

    fun decreaseCartQuantity(item: CartModel) {
        val currentQuantity = item.productCount - 1
        val newModel = item.copy(productCount = currentQuantity)
        if (currentQuantity < 1) removeCartItem(newModel) else updateCartItem(newModel)
    }

    fun increaseCartQuantity(item: CartModel) {
        val currentQuantity = item.productCount + 1
        val newModel = item.copy(productCount = currentQuantity)
        updateCartItem(newModel)
    }

    fun calculateSelectedItems(items: List<CartModel>): Int {
        val totalPayment = items.filter { it.isChecked }.sumOf { it.productCount * it.productPrice }
        return totalPayment
    }

    fun handleSelectAllItem(isChecked: Boolean) {
        cartItems.value.forEach {
            handleOnItemChecked(isChecked, it)
        }
    }

    fun isAllItemSelected(items: List<CartModel>): Boolean {
        return items.all { it.isChecked }
    }
}