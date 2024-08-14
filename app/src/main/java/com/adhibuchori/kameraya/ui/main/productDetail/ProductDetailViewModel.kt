package com.adhibuchori.kameraya.ui.main.productDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhibuchori.data.utils.mapProductDetailToCartModel
import com.adhibuchori.data.utils.mapProductDetailToWishlistModel
import com.adhibuchori.domain.Resource
import com.adhibuchori.domain.repository.cart.AddCartUseCase
import com.adhibuchori.domain.repository.productDetail.IProductDetailRepository
import com.adhibuchori.domain.repository.productDetail.ProductDetailModel
import com.adhibuchori.domain.repository.productDetail.ProductVariant
import com.adhibuchori.domain.repository.wishlist.useCase.AddWishlistItemUseCase
import com.adhibuchori.domain.repository.wishlist.useCase.IsItemInWishlistUseCase
import com.adhibuchori.domain.repository.wishlist.useCase.RemoveWishlistITemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val detailRepository: IProductDetailRepository,
    private val addCartUseCase: AddCartUseCase,
    private val addWishlistItemUseCase: AddWishlistItemUseCase,
    private val removeWishlistITemUseCase: RemoveWishlistITemUseCase,
    private val isItemInWishlistUseCase: IsItemInWishlistUseCase,
) : ViewModel() {

    private val _productDetail = MutableStateFlow<Resource<ProductDetailModel>>(Resource.Loading)
    val productDetail: StateFlow<Resource<ProductDetailModel>> get() = _productDetail

    private val _isItemInWishlist = MutableStateFlow(false)
    val isItemInWishlist = _isItemInWishlist.asStateFlow()

    private fun removeWishlistItem(data: ProductDetailModel?, selectedVariant: ProductVariant?) {
        viewModelScope.launch {
            val wishlistItem = mapProductDetailToWishlistModel(data, selectedVariant)
            removeWishlistITemUseCase.invoke(wishlistItem)
            isItemInWishlist(data, selectedVariant?.variantName)
        }
    }

    fun handleWishlistItem(
        data: ProductDetailModel?,
        selectedVariant: ProductVariant?,
        onHandle: (Boolean) -> Unit,
    ) {
        val status = _isItemInWishlist.value.not()
        onHandle(status)
        if (status) {
            addWishlistItem(data, selectedVariant)
        } else {
            removeWishlistItem(data, selectedVariant)
        }
    }

    private fun addWishlistItem(data: ProductDetailModel?, selectedVariant: ProductVariant?) {
        viewModelScope.launch {
            val wishlistItem = mapProductDetailToWishlistModel(data, selectedVariant)
            addWishlistItemUseCase.invoke(wishlistItem)
            isItemInWishlist(data, selectedVariant?.variantName)
        }
    }

    fun isItemInWishlist(
        data: ProductDetailModel?,
        selectedVariant: String?,
    ) {
        viewModelScope.launch {
            val value = isItemInWishlistUseCase.invoke(
                data?.productId.orEmpty(),
                selectedVariant.orEmpty()
            )
            _isItemInWishlist.getAndUpdate { value }
        }
    }

    fun addProductToCart(data: ProductDetailModel?, selectedVariant: ProductVariant?) {
        viewModelScope.launch {
            val cartItem = mapProductDetailToCartModel(data, selectedVariant)
            addCartUseCase.invoke(cartItem)
        }
    }

    fun fetchProductDetail(id: String) {
        viewModelScope.launch {
            _productDetail.value = detailRepository.getProductDetail(id)
        }
    }
}